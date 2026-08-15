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

    // ─────────────────────────── Statistiky → Stats plugin ───────────────────────────

    public void recordMetric(UUID playerUuid, String playerName, String metric, int amount) {
        if (!statsEnabled || amount <= 0) return;
        final String json = "{\"uuid\":\"" + playerUuid + "\",\"minigame\":\"" + gameId + "\",\"name\":\"" + safe(playerName) + "\",\"" + metric + "\":" + amount + "}";
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(statsUrl + "/api/stats/minigame").openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + statsSecret);
                conn.setDoOutput(true);
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }
                int code = conn.getResponseCode();
                if (code != 200) {
                    plugin.getLogger().warning("Stats push (" + metric + ") returned HTTP " + code);
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
}