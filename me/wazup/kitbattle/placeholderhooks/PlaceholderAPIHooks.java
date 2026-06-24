package me.wazup.kitbattle.placeholderhooks;

import java.math.BigDecimal;
import java.math.RoundingMode;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.managers.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlaceholderAPIHooks extends PlaceholderExpansion {
   private final Kitbattle plugin;

   public PlaceholderAPIHooks(Plugin var1) {
      this.plugin = (Kitbattle)var1;
   }

   public boolean persist() {
      return true;
   }

   public boolean canRegister() {
      return true;
   }

   public String onPlaceholderRequest(Player var1, String var2) {
      if (var2.equals("players_count")) {
         return String.valueOf(this.plugin.players.size());
      } else if (var2.equals("maps_count")) {
         return String.valueOf(this.plugin.playingMaps.size() + this.plugin.tournamentMaps.size() + this.plugin.challengeMaps.size());
      } else if (var2.equals("challengers_count")) {
         return String.valueOf(this.plugin.challengesManager != null ? this.plugin.challengesManager.players.size() : 0);
      } else if (var2.equals("tournament_participants_count")) {
         return String.valueOf(this.plugin.tournamentsManager != null ? this.plugin.tournamentsManager.getSize() : 0);
      } else if (var2.equals("kits_count")) {
         return String.valueOf(this.plugin.Kits.size());
      } else if (var2.equals("ranks_count")) {
         return String.valueOf(this.plugin.Ranks.size());
      } else if (var1 == null) {
         return "";
      } else {
         PlayerData var3 = PlayerDataManager.get(var1);
         if (var3 == null) {
            return "";
         } else if (var2.equals("coins")) {
            return String.valueOf(var3.getCoins(var1));
         } else if (var2.equals("kills")) {
            return String.valueOf(var3.getKills());
         } else if (var2.equals("killstreak")) {
            return String.valueOf(var3.getKillstreak());
         } else if (var2.equals("deathstreak")) {
            return String.valueOf(var3.getDeathstreak());
         } else if (var2.equals("deaths")) {
            return String.valueOf(var3.getDeaths());
         } else if (var2.equals("player_exp")) {
            return String.valueOf(var3.getExp());
         } else if (var2.equals("player_rank")) {
            return String.valueOf(var3.getRank().getName());
         } else if (var2.equals("player_rank_prefix")) {
            return String.valueOf(var3.getRank().getPrefix());
         } else if (var2.equals("player_next_rank")) {
            return String.valueOf(var3.getNextRank() != null ? var3.getNextRank().getName() : "None");
         } else if (var2.equals("player_next_rank_exp")) {
            return String.valueOf(var3.getNextRank() != null ? var3.getNextRank().getRequiredExp() : "0");
         } else if (var2.equals("player_next_rank_exp_difference")) {
            return String.valueOf(var3.getNextRank() != null ? var3.getNextRank().getRequiredExp() - var3.getExp() : "0");
         } else if (var2.equals("elo")) {
            return String.valueOf(var3.getELO());
         } else if (var2.equals("map")) {
            return String.valueOf(var3.getMap() != null ? var3.getMap().name : "None");
         } else if (var2.equals("selected_kit")) {
            return String.valueOf(var3.getKit() != null ? var3.getKit().getName() : "None");
         } else if (var2.equals("ability_cooldown")) {
            return var3.getAbilityCooldown(var1);
         } else if (var2.equals("bounty")) {
            return String.valueOf(var3.getTotalBounty());
         } else if (var2.equals("combat_log_duration")) {
            return String.valueOf(var3.getCombatLogDurationInSeconds());
         } else if (var2.equals("kdr")) {
            double var4 = BigDecimal.valueOf(var3.getDeaths() > 1 ? (double)var3.getKills() / (double)var3.getDeaths() : (double)var3.getKills()).setScale(2, RoundingMode.HALF_UP).doubleValue();
            return String.valueOf(var4);
         } else {
            return null;
         }
      }
   }

   public String getAuthor() {
      return this.plugin.getDescription().getAuthors().toString();
   }

   public String getIdentifier() {
      return "kitbattle";
   }

   public String getVersion() {
      return this.plugin.getDescription().getVersion();
   }
}
