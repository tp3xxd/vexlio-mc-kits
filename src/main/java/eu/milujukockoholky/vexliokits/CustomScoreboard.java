package eu.milujukockoholky.vexliokits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class CustomScoreboard {
   private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
   private Objective objective;
   private final HashMap<Integer, LineEntry> lines = new HashMap();
   private final HashMap<String, PlaceholderEntry> placeholders = new HashMap();
   private final HashMap<Integer, List<String>> multiplePlaceholders = new HashMap();
   private final boolean showHealth;

   public CustomScoreboard(VexlioKits var1, boolean var2, String var3, String... var4) {
      if (var1.config.ScoreboardEnabled) {
         this.objective = this.scoreboard.registerNewObjective("KB", "dummy");
         this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
         this.objective.setDisplayName(this.format(var3));

         for(int var5 = 0; var5 < var4.length; ++var5) {
            while(this.scoreboard.getEntries().contains(var4[var5])) {
               var4[var5] = var4[var5] + " ";
            }

            String var6 = var4[var5];
            String var7 = this.format(var4[var5]);
            int var8 = var4.length - var5;
            this.objective.getScore(var7).setScore(var8);
            this.lines.put(var8, new LineEntry(var6, var7));
            int var9 = this.getCharacterCount(var6, '%');
            if (var9 > 0 && var9 % 2 == 0) {
               if (var9 / 2 > 1) {
                  ArrayList var10 = new ArrayList();

                  for(int var11 = 1; var11 < var9; var11 += 2) {
                     var10.add("%" + var6.split("%")[var11] + "%");
                  }

                  this.multiplePlaceholders.put(var8, var10);
               }

               for(int var13 = 1; var13 < var9; var13 += 2) {
                  this.placeholders.put("%" + var6.split("%")[var13] + "%", new PlaceholderEntry(var8, "N/A"));
               }
            }
         }
      }

      this.showHealth = var2;
      if (var2) {
         Objective var12 = this.scoreboard.registerNewObjective("KB_HEALTH", "health");
         var12.setDisplaySlot(DisplaySlot.BELOW_NAME);
         var12.setDisplayName(ChatColor.DARK_RED + var1.character_heart);
      }

   }

   public void updatePlaceholder(String var1, String var2) {
      if (this.placeholders.containsKey(var1)) {
         PlaceholderEntry var3 = (PlaceholderEntry)this.placeholders.get(var1);
         var3.currentValue = var2;
         int var4 = var3.score;
         LineEntry var5 = (LineEntry)this.lines.get(var4);
         this.scoreboard.resetScores(var5.currentText);
         String var6 = var5.originalText;
         if (this.multiplePlaceholders.containsKey(var4)) {
            for(String var8 : (List<String>)this.multiplePlaceholders.get(var4)) {
               var6 = var6.replace(var8, ((PlaceholderEntry)this.placeholders.get(var8)).currentValue);
            }
         } else {
            var6 = var6.replace(var1, var2);
         }

         while(this.scoreboard.getEntries().contains(var6)) {
            var6 = var6 + " ";
         }

         var6 = this.format(var6);
         this.objective.getScore(var6).setScore(var4);
         var5.currentText = var6;
      }
   }

   public void updatePlaceholder(String var1, int var2) {
      this.updatePlaceholder(var1, String.valueOf(var2));
   }

   private int getCharacterCount(String var1, char var2) {
      int var3 = 0;

      for(char var7 : var1.toCharArray()) {
         if (var7 == var2) {
            ++var3;
         }
      }

      return var3;
   }

   private String format(String var1) {
      return var1.length() > 16 ? (Bukkit.getBukkitVersion().contains("1.7") ? var1.substring(0, 16) : (var1.length() > 32 ? var1.substring(0, 32) : var1)) : var1;
   }

   public void setName(String var1) {
      this.objective.setDisplayName(var1);
   }

   public Team registerTeam(String var1) {
      return this.scoreboard.registerNewTeam(var1);
   }

   public Set<Team> getTeams() {
      return this.scoreboard.getTeams();
   }

   public void apply(Player var1) {
      var1.setScoreboard(this.scoreboard);
      if (this.showHealth && !var1.isDead()) {
         var1.setHealth(var1.getHealth() - 1.0E-4);
      }

   }

   private class PlaceholderEntry {
      int score;
      String currentValue;

      private PlaceholderEntry(int var2, String var3) {
         this.score = var2;
         this.currentValue = var3;
      }
   }

   private class LineEntry {
      String originalText;
      String currentText;

      private LineEntry(String var2, String var3) {
         this.originalText = var2;
         this.currentText = var3;
      }
   }
}
