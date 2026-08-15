package eu.milujukockoholky.vexliokits.minigame;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import eu.milujukockoholky.vexliokits.VexlioKits;
import org.bukkit.Bukkit;

/**
 * Minigame provider (Fáze 7): posílá lifecycle eventy a statistiky do Game API
 * a stats pluginu přes HTTP. kits zůstává minihra plugin — nevlastní queue,
 * web API ani globální leaderboard (spec §9).
 *
 * Lifecycle eventy (spec §7.3): MatchStarted, MatchFinished, PlayerJoined,
 * PlayerLeft, PlayerKilled, PlayerWon, PlayerLost, ObjectiveCompleted.
 */
public class MinigameProvider {

    private final VexlioKits plugin;
    private final Map<UUID, String> matchIds = new ConcurrentHashMap<>();

    // Konfigurovatelné přes config.yml sekce "game-api" a "stats"
    public boolean gameApiEnabled;
    public String gameApiUrl;
    public String gameApiSecret;
    public String gameId = "kits";
    public String modeId = "solo";

    public boolean statsEnabled;
    public String statsUrl;
    public String statsSecret;
    public String serverName = "kits-1";

    // Idempotency pro odměny (spec §20): klíč = matchId + playerUuid + rewardType.
    private final java.util.Set<String> processedRewards = ConcurrentHashMap.newKeySet();

    public MinigameProvider(VexlioKits plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        this.gameApiEnabled = plugin.getConfig().getBoolean("game-api.enabled", false);
        this.gameApiUrl = plugin.getConfig().getString("game-api.url", "http://localhost:3060");
        this.gameApiSecret = plugin.getConfig().getString("game-api.secret", "CHANGE_ME");
        this.gameId = plugin.getConfig().getString("game-api.game-id", "kits");
        this.modeId = plugin.getConfig().getString("game-api.mode-id", "solo");
        this.statsEnabled = plugin.getConfig().getBoolean("stats.enabled", false);
        this.statsUrl = plugin.getConfig().getString("stats.url", "http://localhost:3040");
        this.statsSecret = plugin.getConfig().getString("stats.secret", "CHANGE_ME");
        this.serverName = plugin.getConfig().getString("server.name", "kits-1");
        this.economyEnabled = plugin.getConfig().getBoolean("economy.enabled", false);
        this.economyUrl = plugin.getConfig().getString("economy.url", "https://api.kubiceek.eu");
        this.economySecret = plugin.getConfig().getString("api.key", "CHANGE_ME");
    }

    // ─────────────────────────── Lifecycle eventy → Game API ───────────────────────────

    public void sendMatchStarted(UUID playerUuid) {
        if (!gameApiEnabled) return;
        String matchId = getOrCreateMatchId(playerUuid);
        sendEvent(matchId, "MatchStarted", playerUuid, null);
    }

    public void sendMatchFinished(UUID playerUuid, String winnerTeamId) {
        if (!gameApiEnabled) return;
        String matchId = getOrCreateMatchId(playerUuid);
        sendEvent(matchId, "MatchFinished", playerUuid, winnerTeamId == null ? null : java.util.Collections.singletonMap("winnerTeamId", winnerTeamId));
        matchIds.remove(playerUuid);
    }

    public void sendPlayerJoined(UUID playerUuid) {
        if (!gameApiEnabled) return;
        sendEvent(getOrCreateMatchId(playerUuid), "PlayerJoined", playerUuid, null);
    }

    public void sendPlayerLeft(UUID playerUuid) {
        if (!gameApiEnabled) return;
        String matchId = matchIds.remove(playerUuid);
        if (matchId != null) {
            sendEvent(matchId, "PlayerLeft", playerUuid, null);
        }
    }

    public void sendPlayerKilled(UUID victimUuid, UUID killerUuid) {
        if (!gameApiEnabled) return;
        String matchId = getOrCreateMatchId(victimUuid);
        Map<String, Object> data = killerUuid == null ? null : java.util.Collections.singletonMap("killerUuid", killerUuid.toString());
        sendEvent(matchId, "PlayerKilled", victimUuid, data);
    }

    public void sendPlayerWon(UUID playerUuid) {
        if (!gameApiEnabled) return;
        sendEvent(getOrCreateMatchId(playerUuid), "PlayerWon", playerUuid, null);
    }

    public void sendPlayerLost(UUID playerUuid) {
        if (!gameApiEnabled) return;
        sendEvent(getOrCreateMatchId(playerUuid), "PlayerLost", playerUuid, null);
    }

    private void sendEvent(String matchId, String eventType, UUID playerUuid, Map<String, Object> data) {
        final String json = buildEventJson(matchId, eventType, playerUuid, data);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(gameApiUrl + "/api/game/event").openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + gameApiSecret);
                conn.setDoOutput(true);
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }
                int code = conn.getResponseCode();
                if (code != 200) {
                    plugin.getLogger().warning("Game API event " + eventType + " returned HTTP " + code);
                }
                conn.disconnect();
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to send Game API event " + eventType + ": " + e.getMessage());
            }
        });
    }

    private String buildEventJson(String matchId, String eventType, UUID playerUuid, Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"matchId\":\"").append(matchId)
          .append("\",\"gameId\":\"").append(gameId)
          .append("\",\"modeId\":\"").append(modeId)
          .append("\",\"eventType\":\"").append(eventType)
          .append("\",\"playerUuid\":\"").append(playerUuid)
          .append("\",\"source\":\"kits\"");
        if (data != null && !data.isEmpty()) {
            sb.append(",\"data\":{");
            boolean first = true;
            for (Map.Entry<String, Object> e : data.entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("\"").append(e.getKey()).append("\":\"").append(e.getValue()).append("\"");
            }
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }

    // ─────────────────────────── Statistiky → Game API (MAIN) ───────────────────────────

    /**
     * Statistiky se posílají do Game API (main), ne do stats pluginu (revize).
     * Game API je doplní o identitu serveru z proxy (jméno serveru + minihra)
     * a předá je sync do proxy → core.
     */
    public void recordMetric(UUID playerUuid, String playerName, String metric, int amount) {
        if (!gameApiEnabled || amount <= 0) return;
        final String json = "{\"uuid\":\"" + playerUuid + "\",\"name\":\"" + safe(playerName) + "\",\"metric\":\"" + metric + "\",\"amount\":" + amount + "}";
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(gameApiUrl + "/api/game/stats").openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + gameApiSecret);
                conn.setDoOutput(true);
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }
                int code = conn.getResponseCode();
                if (code != 200) {
                    plugin.getLogger().warning("Game API stats push (" + metric + ") returned HTTP " + code);
                }
                conn.disconnect();
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to push stats (" + metric + "): " + e.getMessage());
            }
        });
    }

    private String safe(String value) {
        return value == null ? "" : value.replace("\"", "").replace("\\", "");
    }

    private String getOrCreateMatchId(UUID playerUuid) {
        return matchIds.computeIfAbsent(playerUuid, k -> UUID.randomUUID().toString());
    }

    // ─────────────────────────── Odměny (spec §20) ───────────────────────────

    public boolean economyEnabled;
    public String economyUrl;
    public String economySecret;

    /**
     * Zapíše odměnu do economy přes core s idempotency klíčem
     * `matchId + playerUuid + rewardType`. Retry/duplicita se neprojeví podruhé.
     * @return true pokud odměna byla skutečně zapsána (poprvé), false při duplikátu/chybě.
     */
    public boolean claimReward(UUID playerUuid, String playerName, String rewardType, long amountMinor) {
        if (!economyEnabled || amountMinor <= 0) return false;
        String matchId = getOrCreateMatchId(playerUuid);
        String idempotencyKey = matchId + ":" + playerUuid + ":" + rewardType;

        if (!processedRewards.add(idempotencyKey)) {
            return false; // už zpracováno — idempotence
        }

        final String json = "{\"uuid\":\"" + playerUuid + "\",\"currency\":\"coins\",\"amount\":" + (amountMinor / 100.0) + ",\"idempotencyKey\":\"" + idempotencyKey + "\"}";
        final java.util.concurrent.atomic.AtomicBoolean success = new java.util.concurrent.atomic.AtomicBoolean(false);
        try {
            java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(economyUrl + "/v1/economy/modify").openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Authorization", "Bearer " + economySecret);
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);
                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(json.getBytes(StandardCharsets.UTF_8));
                    }
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        String body = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                        if (body.contains("\"success\":true")) success.set(true);
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    plugin.getLogger().warning("Reward " + rewardType + " for " + playerUuid + " failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
            latch.await(5, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (success.get()) {
            plugin.getLogger().info("Reward " + rewardType + " (" + amountMinor + " minor) claimed for " + playerUuid + " (key=" + idempotencyKey + ")");
        }
        return success.get();
    }
}