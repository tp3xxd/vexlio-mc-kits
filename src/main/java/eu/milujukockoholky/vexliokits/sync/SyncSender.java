package eu.milujukockoholky.vexliokits.sync;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Sync odesílač (revize architektury).
 *
 * kits nemá vlastní databázi — data drží v paměti/souborech (lokální záloha)
 * a každou minutu posílá snapshot statistik online hráčů plugin messagingem
 * do proxy pluginu (kanál "vexlio:sync:v1"), který je uloží do SQLite a přepošle
 * na core HTTP API.
 */
public class SyncSender {

    private static final String CHANNEL = "vexlio:sync";

    private final VexlioKits plugin;
    private final ConcurrentLinkedQueue<String> pendingBatches = new ConcurrentLinkedQueue<>();
    private boolean running = false;

    public SyncSender(VexlioKits plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (running) return;
        running = true;
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            buildSnapshot();
            flush();
        }, 20L * 60, 20L * 60);
        plugin.getLogger().info("SyncSender started (channel " + CHANNEL + ").");
    }

    public void enqueueBatch(String batchJson) {
        if (batchJson == null || batchJson.isEmpty()) return;
        pendingBatches.add(batchJson);
    }

    public void flush() {
        if (pendingBatches.isEmpty()) return;

        String json = pendingBatches.poll();
        if (json == null) return;

        byte[] data = json.getBytes(StandardCharsets.UTF_8);

        // Plugin messaging z Paper do proxy prochází připojeným hráčem.
        Player target = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            target = p;
            break;
        }
        if (target == null) {
            pendingBatches.add(json);
            return;
        }

        try {
            target.sendPluginMessage(plugin, CHANNEL, json.getBytes(StandardCharsets.UTF_8));
            plugin.getLogger().info("Sent sync batch to proxy.");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send sync batch to proxy: " + e.getMessage());
            pendingBatches.add(json);
        }
    }

    /**
     * Sestaví snapshot statistik online hráčů a zařadí ho do fronty.
     */
    public void buildSnapshot() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"version\":1,\"source\":\"").append(safe(plugin.getConfig().getString("server.name", "kits-1")))
          .append("\",\"sentAt\":\"").append(java.time.Instant.now().toString()).append("\",\"events\":[");
        boolean first = true;
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerData data = PlayerDataManager.get(p);
            if (data == null) continue;
            if (!first) sb.append(",");
            first = false;
            sb.append("{\"type\":\"KITS_PLAYER\",\"timestamp\":\"")
              .append(java.time.Instant.now().toString())
              .append("\",\"payload\":{\"playerUuid\":\"").append(p.getUniqueId())
              .append("\",\"playerName\":\"").append(safe(p.getName()))
              .append("\",\"kills\":").append(data.getKills())
              .append(",\"deaths\":").append(data.getDeaths())
              .append(",\"elo\":").append(data.getELO())
              .append(",\"coins\":").append(data.getCoins(p))
              .append(",\"exp\":").append(data.getExp()).append("}}");
        }
        sb.append("]}");
        enqueueBatch(sb.toString());
    }

    private String safe(String value) {
        return value == null ? "" : value.replace("\"", "").replace("\\", "");
    }
}