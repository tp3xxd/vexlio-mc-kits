package eu.milujukockoholky.vexliokits;

import java.util.ArrayList;
import java.util.HashMap;
import eu.milujukockoholky.vexliokits.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;

public class Messages {
   public String prefix;
   public HashMap<String, String> messages;
   public HashMap<String, String> titles;
   public HashMap<String, String> deathMessages;
   public HashMap<String, String> stats;
   public HashMap<String, String> inventories;
   public ArrayList<String> killMessages;
   ArrayList<String> rankedMessages;
   public ArrayList<String> achievementMessages;
   String scoreboard_title;
   String challenge_scoreboard_title;
   String[] defaultScoreboard;
   String[] challengeScoreboard;
   public String holographicLeaderboardHeader;
   public String holographicLeaderboardBody;
   public String holographicLeaderboardFooter;
   String statsCommandHeader;
   String statsCommandBody;
   String statsCommandFooter;
   private static Messages instance;

   public static Messages getInstance() {
      return instance;
   }

   public Messages() {
      instance = this;
   }

   public void loadMessages(FileConfiguration var1) {
      this.prefix = this.c(var1, "prefix");
      this.messages = new HashMap();

      for(String var3 : var1.getConfigurationSection("Messages").getKeys(false)) {
         this.messages.put(var3, this.prefix + this.c(var1, "Messages." + var3));
      }

      this.titles = new HashMap();

      for(String var13 : var1.getConfigurationSection("Titles").getKeys(false)) {
         this.titles.put(var13, this.c(var1, "Titles." + var13));
      }

      this.deathMessages = new HashMap();

      for(String var14 : var1.getConfigurationSection("Death-Messages").getKeys(false)) {
         if (!var14.equalsIgnoreCase("ANOTHER-PLAYER")) {
            this.deathMessages.put(var14.toUpperCase(), this.prefix + this.c(var1, "Death-Messages." + var14));
         }
      }

      this.stats = new HashMap();

      for(String var15 : var1.getConfigurationSection("Stats").getKeys(false)) {
         this.stats.put(var15, this.c(var1, "Stats." + var15));
      }

      this.killMessages = new ArrayList();

      for(String var16 : var1.getStringList("Death-Messages.ANOTHER-PLAYER")) {
         this.killMessages.add(this.prefix + Utils.colorize(var16));
      }

      this.rankedMessages = new ArrayList();

      for(String var17 : var1.getStringList("Ranked-Messages")) {
         this.rankedMessages.add(this.prefix + Utils.colorize(var17));
      }

      this.achievementMessages = new ArrayList();

      for(String var18 : var1.getStringList("Achievement-Unlock")) {
         this.achievementMessages.add(Utils.colorize(var18));
      }

      this.scoreboard_title = this.c(var1, "Scoreboard.title");
      this.defaultScoreboard = new String[var1.getStringList("Scoreboard.content").size()];
      int var11 = 0;

      for(String var4 : var1.getStringList("Scoreboard.content")) {
         this.defaultScoreboard[var11++] = Utils.colorize(var4);
      }

      this.challenge_scoreboard_title = this.c(var1, "Challenge-Scoreboard.title");
      this.challengeScoreboard = new String[var1.getStringList("Challenge-Scoreboard.content").size()];
      var11 = 0;

      for(String var22 : var1.getStringList("Challenge-Scoreboard.content")) {
         this.challengeScoreboard[var11++] = Utils.colorize(var22);
      }

      this.inventories = new HashMap();

      for(String var23 : var1.getConfigurationSection("Inventories").getKeys(false)) {
         this.inventories.put(var23, Utils.colorize(var1.getString("Inventories." + var23)));
      }

      this.holographicLeaderboardHeader = this.c(var1, "Holographic-Leaderboard-Styling.Header");
      this.holographicLeaderboardBody = this.c(var1, "Holographic-Leaderboard-Styling.Body");
      this.holographicLeaderboardFooter = this.c(var1, "Holographic-Leaderboard-Styling.Footer");
      this.statsCommandHeader = this.c(var1, "Stats-Command-Format.Header");
      this.statsCommandBody = this.c(var1, "Stats-Command-Format.Body");
      this.statsCommandFooter = this.c(var1, "Stats-Command-Format.Footer");
   }

   private String c(FileConfiguration var1, String var2) {
      return Utils.colorize(var1.getString(var2));
   }
}
