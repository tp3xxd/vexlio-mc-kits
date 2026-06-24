package eu.milujukockoholky.vexliokits;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import eu.milujukockoholky.vexliokits.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Config {
   public boolean DisableInteractionsInSpawnRegion;
   public boolean UUID;
   int StartingCoins;
   int StartingELO;
   int EloChangeFactor;
   public boolean ScoreboardEnabled;
   public boolean KillCoinsContribution;
   public boolean DoPlayersDropItemsOnDeath;
   public boolean CanPlayersPickItemsOnGround;
   public boolean CanPlayersDropItemsOnGround;
   boolean NotifyWhenCooldownOff;
   public boolean DoPlayersLoseHunger;
   public int EarnedCoinsPerKill;
   public ArrayList<String> defaultKits;
   public ArrayList<Integer> possibleExp;
   public ArrayList<String> allowedCommands;
   public ArrayList<String> aliases;
   boolean BroadcastRankUp;
   public boolean PurchaseableKitsArePermanent;
   boolean showHealthBelowName;
   public boolean ShowRankInChat;
   int StartingAmountOfKitUnlockers;
   public boolean respawnScreenOnDeath;
   public int spawnTeleportCountdownSeconds;
   public int spectateCountdownSeconds;
   public double SellValue;
   public boolean SendDeathMessageToEveryone;
   public double SpongeBoostUpwards;
   public double SpongeFallProtection;
   public boolean SoupAutoDisappear;
   public boolean FallDamageEnabled;
   public boolean OpenKitsMenuOnRespawn;
   public boolean SendKillstreaksToEveryone;
   public boolean useMySQL;
   int ChallengeLives;
   public int ChallengeRespawnProtectionSeconds;
   public boolean AllowBuilding;
   public boolean SoupDropSound;
   public boolean SpongeLaunchSound;
   public boolean VoidInstantDeath;
   public int LeaveCommandTimer;
   public int MinimumBounty;
   public int BountySetCooldown;
   public int SoupSignCooldown;
   public HashMap<Integer, List<String>> Killstreaks;
   public HashMap<Integer, List<String>> Deathstreaks;
   public List<String> DeathstreakEndCommands;
   public int leastDeathstreak;
   int UpdateTopSignsEveryInMinutes;
   int mapLoadDelay;
   HashMap<String, Double> modifiers;
   public boolean bungeeMode;
   int shuffleEveryMinutes;
   int highestTimeShownBeforeShuffle;
   List<Integer> timeShownBeforeShuffle;
   int tournamentsMinPlayers;
   int tournamentsGracePeriod;
   int tournamentsCountdownLength;
   int maxTournamentsTime;
   int celebrationLength;
   boolean tournamentsFireworksCelebration;
   List<String> tournamentsWinnerCommands;
   List<Integer> tournamentsTimeShownDuringCountdown;
   List<Integer> tournamentsGracePeriodWarnings;
   List<Integer> tournamentsEndWarnings;
   int tournamentsGracePeriodWarningsMax;
   int tournamentsEndWarningsMax;
   int challengesGraceLength;
   int maxChallengeTime;
   int challengeCelebrationLength;
   boolean challengesFireworksCelebration;
   List<Integer> challengesGracePeriodWarnings;
   List<Integer> challengesEndWarnings;
   int challengesGracePeriodWarningsMax;
   int challengesEndWarningsMax;
   List<String> challengesWinnerCommands;
   boolean challengeKitLock;
   String challengeKit;
   boolean TrailsEnabled;
   public int TrailsSize;
   public int TrailsInterval;
   boolean scoreboardTitleAnimationEnabled;
   int scoreboardTitleAnimationInterval;
   List<String> scoreboardTitleAnimationFrames;
   boolean abilityActionbarCooldown;
   int actionBarUpdateSpeed;
   DecimalFormat actionBarTimeFormat;
   double actionBarDecrement;
   List<String> kitLoresOwned;
   List<String> kitLoresShop;
   public String tableprefix;
   String mysqlhost;
   String mysqlport;
   String mysqldatabase;
   String mysqlusername;
   String mysqlpassword;
   public Material achievementLocked;
   public Material achievementUnlocked;
   public String achievementLockedLore;
   public String achievementUnlockedLore;
   public String achievementDescription;
   public String achievementPrize;
   public String SignsPrefix;
   public String JoinPrefix;
   public String LeavePrefix;
   public String SoupPrefix;
   public String StatsPrefix;
   public String PotionsPrefix;
   public ChatColor JoinLine3Color;
   public List<PotionEffect> respawnEffects;
   public boolean combatLogEnabled;
   public boolean combatLogMessage;
   public int combatLogDuration;
   public HashMap<String, HotbarItem> hotBarItems;
   private static Config instance;

   public static Config getInstance() {
      return instance;
   }

   public Config() {
      this.SignsPrefix = ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "KB" + ChatColor.DARK_AQUA + "]";
      this.JoinPrefix = ChatColor.AQUA + "Join";
      this.LeavePrefix = ChatColor.AQUA + "Leave";
      this.SoupPrefix = ChatColor.AQUA + "Soup";
      this.StatsPrefix = ChatColor.AQUA + "Stats";
      this.PotionsPrefix = ChatColor.AQUA + "Potions";
      this.JoinLine3Color = ChatColor.BLACK;
      instance = this;
   }

   public void loadConfig() {
      FileConfiguration var1 = VexlioKits.getInstance().getConfig();
      this.UUID = var1.getBoolean("use-UUID");
      this.StartingCoins = var1.getInt("Starting-Coins");
      this.StartingELO = var1.getInt("ELO.Starting-Value");
      this.EloChangeFactor = var1.getInt("ELO.Change-Factor");
      this.ScoreboardEnabled = var1.getBoolean("Scoreboard-Enabled");
      this.KillCoinsContribution = var1.getBoolean("Kill-Coins-Contribution");
      this.DoPlayersDropItemsOnDeath = var1.getBoolean("Do-Players-Drop-Items-On-Death");
      this.CanPlayersPickItemsOnGround = var1.getBoolean("Can-Players-Pick-Items-On-Ground");
      this.CanPlayersDropItemsOnGround = var1.getBoolean("Can-Players-Drop-Items-On-Ground");
      this.DisableInteractionsInSpawnRegion = var1.getBoolean("Disable-Interactions-In-Spawn-Region");
      this.showHealthBelowName = var1.getBoolean("Show-Health-Below-Name");
      this.NotifyWhenCooldownOff = var1.getBoolean("Notify-When-Cooldown-Off");
      this.DoPlayersLoseHunger = var1.getBoolean("Do-Players-Lose-Hunger");
      this.EarnedCoinsPerKill = var1.getInt("Earned-Coins-Per-Kill");
      this.defaultKits = new ArrayList();

      for(String var3 : var1.getStringList("Default-Kits")) {
         this.defaultKits.add(var3.toLowerCase());
      }

      this.possibleExp = new ArrayList();

      for(int var9 = var1.getInt("Minimum-Exp-Per-Kill"); var9 < var1.getInt("Maximum-Exp-Per-Kill") + 1; ++var9) {
         this.possibleExp.add(var9);
      }

      this.allowedCommands = new ArrayList();

      for(String var23 : var1.getStringList("Allowed-Commands")) {
         this.allowedCommands.add(var23.toLowerCase());
      }

      this.aliases = new ArrayList();

      for(String var24 : var1.getStringList("Aliases")) {
         this.aliases.add(var24.toLowerCase());
      }

      this.mapLoadDelay = var1.getInt("Load-Delay");
      this.BroadcastRankUp = var1.getBoolean("Broadcast-Rank-Up");
      this.SellValue = var1.getDouble("Sell-Value");
      this.PurchaseableKitsArePermanent = var1.getBoolean("Purchaseable-Kits-Are-Permanent");
      this.ShowRankInChat = var1.getBoolean("Show-Rank-In-Chat");
      this.StartingAmountOfKitUnlockers = var1.getInt("Starting-Amount-Of-Kit-Unlockers");
      this.respawnScreenOnDeath = var1.getBoolean("Respawn-Screen-On-Death");
      this.spawnTeleportCountdownSeconds = var1.getInt("Spawn-Teleport-Countdown-Seconds");
      this.spectateCountdownSeconds = var1.getInt("Spectate-Countdown-Seconds");
      this.SendDeathMessageToEveryone = var1.getBoolean("Send-Death-Message-To-Everyone");
      this.SpongeBoostUpwards = var1.getDouble("Sponge-Boost-Upwards");
      this.SpongeFallProtection = this.SpongeBoostUpwards * this.SpongeBoostUpwards * (double)3.0F;
      this.SoupAutoDisappear = var1.getBoolean("Soup-Auto-Disappear");
      this.FallDamageEnabled = var1.getBoolean("Fall-Damage-Enabled");
      this.OpenKitsMenuOnRespawn = var1.getBoolean("Open-Kits-Menu-On-Respawn");
      this.ChallengeLives = var1.getInt("Challenge-Lives");
      this.ChallengeRespawnProtectionSeconds = var1.getInt("Challenge-Respawn-Protection-Seconds") + 1;
      this.MinimumBounty = var1.getInt("Minimum-Bounty");
      this.AllowBuilding = var1.getBoolean("Allow-Building");
      this.SoupDropSound = var1.getBoolean("Soup-Drop-Sound");
      this.SpongeLaunchSound = var1.getBoolean("Sponge-Launch-Sound");
      this.LeaveCommandTimer = var1.getInt("Leave-Command-Timer");
      this.BountySetCooldown = var1.getInt("Bounty-Set-Cooldown");
      this.SoupSignCooldown = var1.getInt("Soup-Sign-Cooldown");
      this.VoidInstantDeath = var1.getBoolean("Void-Instant-Death");
      this.combatLogEnabled = var1.getBoolean("Combat-Log.enabled");
      this.combatLogMessage = var1.getBoolean("Combat-Log.message");
      this.combatLogDuration = var1.getInt("Combat-Log.duration");
      this.modifiers = new HashMap();

      for(String var25 : var1.getStringList("Modifiers")) {
         this.modifiers.put(var25.split(" : ")[1], Double.valueOf(var25.split(" : ")[0]));
      }

      this.bungeeMode = var1.getBoolean("Bungee-Mode");
      this.shuffleEveryMinutes = var1.getInt("Shuffle-Every-In-Minutes");
      this.timeShownBeforeShuffle = var1.getIntegerList("Time-Shown-Before-Shuffle");
      this.highestTimeShownBeforeShuffle = 0;

      for(int var26 : this.timeShownBeforeShuffle) {
         if (var26 > this.highestTimeShownBeforeShuffle) {
            this.highestTimeShownBeforeShuffle = var26;
         }
      }

      this.tournamentsMinPlayers = var1.getInt("Tournaments.min-players");
      this.tournamentsGracePeriod = var1.getInt("Tournaments.grace-period");
      this.tournamentsCountdownLength = var1.getInt("Tournaments.countdown-length");
      this.maxTournamentsTime = var1.getInt("Tournaments.max-time");
      this.celebrationLength = var1.getInt("Tournaments.celebration-length");
      this.tournamentsFireworksCelebration = var1.getBoolean("Tournaments.fireworks-celebration");
      this.tournamentsWinnerCommands = var1.getStringList("Tournaments.winner-rewards");
      this.tournamentsTimeShownDuringCountdown = var1.getIntegerList("Tournaments.time-shown-during-countdown");
      this.tournamentsGracePeriodWarnings = var1.getIntegerList("Tournaments.grace-period-warnings");
      this.tournamentsEndWarnings = var1.getIntegerList("Tournaments.end-warnings");
      this.tournamentsGracePeriodWarningsMax = 0;

      for(int var27 : this.tournamentsGracePeriodWarnings) {
         if (var27 > this.tournamentsGracePeriodWarningsMax) {
            this.tournamentsGracePeriodWarningsMax = var27;
         }
      }

      this.tournamentsEndWarningsMax = 0;

      for(int var28 : this.tournamentsEndWarnings) {
         if (var28 > this.tournamentsEndWarningsMax) {
            this.tournamentsEndWarningsMax = var28;
         }
      }

      this.maxChallengeTime = var1.getInt("Challenges.max-time");
      this.challengesGraceLength = var1.getInt("Challenges.grace-length");
      this.challengeCelebrationLength = var1.getInt("Challenges.celebration-length");
      this.challengesFireworksCelebration = var1.getBoolean("Tournaments.fireworks-celebration");
      this.challengesGracePeriodWarnings = var1.getIntegerList("Challenges.grace-period-warnings");
      this.challengesEndWarnings = var1.getIntegerList("Challenges.end-warnings");
      this.challengesWinnerCommands = var1.getStringList("Challenges.winner-rewards");
      this.challengeKitLock = var1.getBoolean("Challenges.kit-lock.enabled");
      this.challengeKit = var1.getString("Challenges.kit-lock.challenge-kit");
      this.challengesGracePeriodWarningsMax = 0;

      for(int var29 : this.challengesGracePeriodWarnings) {
         if (var29 > this.challengesGracePeriodWarningsMax) {
            this.challengesGracePeriodWarningsMax = var29;
         }
      }

      this.challengesEndWarningsMax = 0;

      for(int var30 : this.challengesEndWarnings) {
         if (var30 > this.challengesEndWarningsMax) {
            this.challengesEndWarningsMax = var30;
         }
      }

      this.scoreboardTitleAnimationEnabled = var1.getBoolean("Scoreboard-Title-Animation.enabled");
      this.scoreboardTitleAnimationInterval = var1.getInt("Scoreboard-Title-Animation.interval");
      this.scoreboardTitleAnimationFrames = new ArrayList();

      for(String var31 : var1.getStringList("Scoreboard-Title-Animation.frames")) {
         this.scoreboardTitleAnimationFrames.add(Utils.colorize(var31));
      }

      if (this.scoreboardTitleAnimationFrames.size() < 2) {
         this.scoreboardTitleAnimationEnabled = false;
         Bukkit.getConsoleSender().sendMessage(VexlioKits.getInstance().msgs.prefix + "Scoreboard animation was disabled because there are not enough amount of frames!");
      } else if (this.scoreboardTitleAnimationEnabled) {
         Bukkit.getConsoleSender().sendMessage(VexlioKits.getInstance().msgs.prefix + "Scoreboard title animation has been enabled and it contains " + ChatColor.AQUA + this.scoreboardTitleAnimationFrames.size() + ChatColor.GRAY + " frame(s)!");
      }

      this.Killstreaks = new HashMap();
      if (var1.getConfigurationSection("Killstreaks") == null) {
         var1.set("Killstreaks.3.Commands-Executed", Arrays.asList("vexliokits coins add %player% 20"));
         var1.set("Killstreaks.5.Commands-Executed", Arrays.asList("vexliokits coins add %player% 30"));
         var1.set("Killstreaks.10.Commands-Executed", Arrays.asList("vexliokits coins add %player% 50"));
         var1.set("Killstreaks.20.Commands-Executed", Arrays.asList("vexliokits coins add %player% 100"));
         var1.set("Killstreaks.30.Commands-Executed", Arrays.asList("vexliokits coins add %player% 200"));
         VexlioKits.getInstance().saveConfig();
      }

      if (var1.getBoolean("Killstreaks-Enabled")) {
         for(String var32 : var1.getConfigurationSection("Killstreaks").getKeys(false)) {
            this.Killstreaks.put(Integer.valueOf(var32), var1.getStringList("Killstreaks." + var32 + ".Commands-Executed"));
         }
      }

      this.kitLoresOwned = var1.getStringList("Kit-Lores.Owned");
      this.kitLoresShop = var1.getStringList("Kit-Lores.Shop");
      this.TrailsEnabled = var1.getBoolean("Trails.enabled");
      this.TrailsSize = var1.getInt("Trails.size");
      this.TrailsInterval = var1.getInt("Trails.interval");
      this.Deathstreaks = new HashMap();
      this.leastDeathstreak = Integer.MAX_VALUE;
      if (var1.getConfigurationSection("Deathstreaks") == null) {
         var1.set("Deathstreaks.5.Commands-Executed", Arrays.asList("effect %player% resistance 9999 0"));
         var1.set("Deathstreaks.8.Commands-Executed", Arrays.asList("effect %player% resistance 9999 0", "effect %player% regeneration 9999 1"));
         var1.set("Deathstreaks.10.Commands-Executed", Arrays.asList("effect %player% resistance 9999 0", "effect %player% regeneration 9999 1", "effect %player% strength 9999 0", "effect %player% speed 9999 0"));
         VexlioKits.getInstance().saveConfig();
      }

      if (var1.getBoolean("Deathstreaks-Enabled")) {
         for(String var33 : var1.getConfigurationSection("Deathstreaks").getKeys(false)) {
            int var4 = Integer.valueOf(var33);
            this.Deathstreaks.put(var4, var1.getStringList("Deathstreaks." + var33 + ".Commands-Executed"));
            if (var4 < this.leastDeathstreak) {
               this.leastDeathstreak = var4;
            }
         }
      }

      this.DeathstreakEndCommands = var1.getStringList("Deathstreak-End-Commands");
      this.respawnEffects = new ArrayList();

      for(String var34 : var1.getStringList("Respawn-Effects")) {
         String[] var37 = var34.split(" : ");
         PotionEffectType var5 = PotionEffectType.getByName(var37[0].toUpperCase());
         int var6 = Integer.parseInt(var37[1]) * 20;
         int var7 = Integer.parseInt(var37[2]) - 1;
         PotionEffect var8 = new PotionEffect(var5, var6, var7);
         this.respawnEffects.add(var8);
      }

      this.SendKillstreaksToEveryone = var1.getBoolean("Send-Killstreaks-To-Everyone");
      this.UpdateTopSignsEveryInMinutes = var1.getInt("Update-Top-Signs-Every-In-Minutes");
      this.useMySQL = var1.getBoolean("use-mysql");
      this.tableprefix = var1.getString("table-prefix");
      this.mysqlhost = var1.getString("mysql-host");
      this.mysqlport = var1.getString("mysql-port");
      this.mysqldatabase = var1.getString("mysql-database");
      this.mysqlusername = var1.getString("mysql-username");
      this.mysqlpassword = var1.getString("mysql-password");
      this.abilityActionbarCooldown = var1.getBoolean("Action-Bar.Ability-Cooldown.enabled");
      this.actionBarUpdateSpeed = 21 - Math.max(Math.min(var1.getInt("Action-Bar.Ability-Cooldown.update-speed"), 20), 1);
      this.actionBarTimeFormat = new DecimalFormat(var1.getString("Action-Bar.Ability-Cooldown.time-format"));
      this.actionBarDecrement = (double)this.actionBarUpdateSpeed / (double)20.0F;
      FileConfiguration var22 = VexlioKits.getInstance().fileManager.getConfig("achievements.yml");
      this.achievementLocked = Material.getMaterial(var22.getString("Styling.Locked-Material"));
      this.achievementUnlocked = Material.getMaterial(var22.getString("Styling.Unlocked-Material"));
      this.achievementLockedLore = Utils.colorize(var22.getString("Styling.Locked-Lore"));
      this.achievementUnlockedLore = Utils.colorize(var22.getString("Styling.Unlocked-Lore"));
      this.achievementDescription = Utils.colorize(var22.getString("Styling.Name"));
      this.achievementPrize = Utils.colorize(var22.getString("Styling.Prize"));
      this.hotBarItems = new HashMap();

      for(String var38 : var1.getConfigurationSection("Hotbar-Items").getKeys(false)) {
         boolean var39 = var1.getBoolean("Hotbar-Items." + var38 + ".enabled");
         ItemStack var40 = Utils.getItemStack(var1.getString("Hotbar-Items." + var38 + ".item"), false, true);
         int var41 = var1.getInt("Hotbar-Items." + var38 + ".slot") - 1;
         this.hotBarItems.put(var38, new HotbarItem(var40, var41, var39));
      }

      FileConfiguration var36 = VexlioKits.getInstance().fileManager.getConfig("signs.yml");
      this.SignsPrefix = Utils.colorize(var36.getString("Signs-Prefix"));
      this.JoinPrefix = Utils.colorize(var36.getString("Join-Prefix"));
      this.LeavePrefix = Utils.colorize(var36.getString("Leave-Prefix"));
      this.SoupPrefix = Utils.colorize(var36.getString("Soup-Prefix"));
      this.StatsPrefix = Utils.colorize(var36.getString("Stats-Prefix"));
      this.PotionsPrefix = Utils.colorize(var36.getString("Potions-Prefix"));
      this.JoinLine3Color = ChatColor.getByChar(var36.getString("Join-Line-3-Color").replace("&", ""));
   }

   public class HotbarItem {
      public ItemStack item;
      public int slot;
      public boolean enabled;

      public HotbarItem(ItemStack var2, int var3, boolean var4) {
         this.item = var2;
         this.slot = var3;
         this.enabled = var4;
      }
   }
}
