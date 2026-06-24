package me.wazup.kitbattle.placeholderhooks;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.managers.PlayerDataManager;

public class MVdWPlacholderHook {
   public MVdWPlacholderHook(final Kitbattle var1) {
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_players_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.players.size());
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_maps_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.playingMaps.size() + var1.tournamentMaps.size() + var1.challengeMaps.size());
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_challengers_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.challengesManager != null ? var1.challengesManager.players.size() : 0);
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_tournament_participants_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.tournamentsManager != null ? var1.tournamentsManager.getSize() : 0);
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_kits_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.Kits.size());
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_ranks_count", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1x) {
            return String.valueOf(var1.Ranks.size());
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_coins", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1) {
            return var1.isOnline() ? String.valueOf(PlayerDataManager.get(var1.getPlayer()).getCoins(var1.getPlayer())) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_kills", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1) {
            return var1.isOnline() ? String.valueOf(PlayerDataManager.get(var1.getPlayer()).getKills()) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_killstreak", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1) {
            return var1.isOnline() ? String.valueOf(PlayerDataManager.get(var1.getPlayer()).getKillstreak()) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_deaths", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1) {
            return var1.isOnline() ? String.valueOf(PlayerDataManager.get(var1.getPlayer()).getDeaths()) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_player_exp", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1) {
            return var1.isOnline() ? String.valueOf(PlayerDataManager.get(var1.getPlayer()).getExp()) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_player_rank", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1) {
            return var1.isOnline() ? String.valueOf(PlayerDataManager.get(var1.getPlayer()).getRank().getName()) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_player_next_rank", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1) {
            return var1.isOnline() ? String.valueOf(PlayerDataManager.get(var1.getPlayer()).getNextRank() != null ? PlayerDataManager.get(var1.getPlayer()).getNextRank().getName() : "None") : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_player_next_rank_exp", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1) {
            return var1.isOnline() ? String.valueOf(PlayerDataManager.get(var1.getPlayer()).getNextRank() != null ? PlayerDataManager.get(var1.getPlayer()).getNextRank().getRequiredExp() : "0") : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_player_next_rank_exp_difference", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1) {
            return var1.isOnline() ? String.valueOf(PlayerDataManager.get(var1.getPlayer()).getNextRank() != null ? PlayerDataManager.get(var1.getPlayer()).getNextRank().getRequiredExp() - PlayerDataManager.get(var1.getPlayer()).getExp() : "0") : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_elo", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1) {
            return var1.isOnline() ? String.valueOf(PlayerDataManager.get(var1.getPlayer()).getELO()) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_map", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1) {
            return var1.isOnline() ? String.valueOf(PlayerDataManager.get(var1.getPlayer()).getMap() != null ? PlayerDataManager.get(var1.getPlayer()).getMap().name : "None") : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_selected_kit", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1) {
            return var1.isOnline() ? String.valueOf(PlayerDataManager.get(var1.getPlayer()).getKit() != null ? PlayerDataManager.get(var1.getPlayer()).getKit().getName() : "None") : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_ability_cooldown", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1) {
            return var1.isOnline() ? String.valueOf(PlayerDataManager.get(var1.getPlayer()).getAbilityCooldown(var1.getPlayer())) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_bounty", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1) {
            return var1.isOnline() ? String.valueOf(PlayerDataManager.get(var1.getPlayer()).getTotalBounty()) : "";
         }
      });
      PlaceholderAPI.registerPlaceholder(var1, "kitbattle_kdr", new PlaceholderReplacer() {
         public String onPlaceholderReplace(PlaceholderReplaceEvent var1) {
            if (var1.isOnline()) {
               PlayerData var2 = PlayerDataManager.get(var1.getPlayer());
               double var3 = BigDecimal.valueOf(var2.getDeaths() > 1 ? (double)var2.getKills() / (double)var2.getDeaths() : (double)var2.getKills()).setScale(2, RoundingMode.HALF_UP).doubleValue();
               return String.valueOf(var3);
            } else {
               return "";
            }
         }
      });
   }
}
