package me.wazup.kitbattle;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import me.wazup.kitbattle.managers.AchievementsManager;
import me.wazup.kitbattle.managers.PlayerDataManager;
import me.wazup.kitbattle.managers.TitleManager;
import me.wazup.kitbattle.utils.ItemStackBuilder;
import me.wazup.kitbattle.utils.SmartInventory;
import me.wazup.kitbattle.utils.Utils;
import me.wazup.kitbattle.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerData {
   private ItemStack[] items;
   private ItemStack[] armor;
   private Location location;
   private double health;
   private int food;
   private int levels;
   private float exp;
   private Scoreboard scoreboard;
   private Collection<PotionEffect> potions;
   private GameMode mode;
   private boolean flying;
   public boolean joined = false;
   public SmartInventory kitsInventory;
   public SmartInventory upgradesInventory;
   public SmartInventory achievements;
   public int killstreak = 0;
   public int deathstreak = 0;
   public HashMap<String, Double> damagers = new HashMap();
   public long lastHitTime = 0L;
   public String lastHit = "";
   public HashMap<String, Integer> bounties = new HashMap();
   public CustomScoreboard customScoreboard;
   String lastAbilityKey;
   private final Kitbattle plugin = Kitbattle.getInstance();
   private PlayingMap map;
   private Kit kit;
   private final HashMap<String, Long> cooldowns = new HashMap();
   private final HashMap<String, Long> kitSelectionCooldown = new HashMap();
   private BukkitTask abilityCooldownNotifier;
   public BukkitTask teleportTask;
   public int coins = 0;
   public int kitUnlockers = 0;
   Rank rank;
   Rank nextRank;
   public Effect selectedTrail;
   private int kills = 0;
   private int deaths = 0;
   private int dataexp = 0;
   private int projectiles_hit = 0;
   private int tournament_wins = 0;
   private int challenge_wins = 0;
   private int abilities_used = 0;
   private int soups_eaten = 0;
   private int killstreaks_earned = 0;
   private int elo = 0;

   public PlayerData(Player var1) {
      this.loadStats(var1);
   }

   public void loadStats(final Player var1) {
      final String var2 = this.plugin.config.UUID ? var1.getUniqueId().toString() : var1.getName();
      (new BukkitRunnable() {
         public void run() {
            ArrayList var1x = new ArrayList();
            boolean var2x = false;
            if (PlayerData.this.plugin.config.useMySQL) {
               try {
                  Statement var3 = PlayerData.this.plugin.mysql.getConnection().createStatement();
                  String var4 = PlayerData.this.plugin.config.tableprefix;
                  if (var3.executeQuery("SELECT * FROM " + var4 + " WHERE " + (PlayerData.this.plugin.config.UUID ? "player_uuid" : "player_name") + " = '" + var2 + "';").next()) {
                     ResultSet var5 = var3.executeQuery("SELECT * FROM " + var4 + " WHERE " + (PlayerData.this.plugin.config.UUID ? "player_uuid" : "player_name") + " = '" + var2 + "';");
                     var5.next();
                     String[] var6 = var5.getString("Kits").split(", ");

                     for(int var7 = 0; var7 < var6.length; ++var7) {
                        var1x.add(var6[var7].toLowerCase());
                     }

                     try {
                        PlayerData.this.loadStatisticsString(var5.getString("Statistics"));
                     } catch (Exception var8) {
                     }

                     var3.close();
                     var5.close();
                  } else {
                     PlayerData.this.resetPlayer(var1);
                     var2x = true;
                  }
               } catch (SQLException var9) {
                  var9.printStackTrace();
               }
            } else {
               File var10 = new File(PlayerData.this.plugin.getDataFolder() + "/players/", var2);
               if (var10.exists()) {
                  YamlConfiguration var11 = YamlConfiguration.loadConfiguration(var10);

                  for(String var13 : ((FileConfiguration)var11).getStringList("Kits")) {
                     var1x.add(var13.toLowerCase());
                  }

                  PlayerData.this.loadStatisticsString(((FileConfiguration)var11).getString("Statistics"));
               } else {
                  PlayerData.this.resetPlayer(var1);
                  var2x = true;
               }
            }

            PlayerData.this.updateRank();
            PlayerData.this.achievements = PlayerData.this.plugin.achievementsManager.getAchievements(PlayerData.this);
            if (!var2x) {
               PlayerData.this.loadKits(var1x);
            }

            if (PlayerData.this.plugin.players.contains(var1.getUniqueId())) {
               (new BukkitRunnable() {
                  public void run() {
                     PlayerData.this.createScoreboard(var1);
                  }
               }).runTask(Kitbattle.getInstance());
            }

         }
      }).runTaskLaterAsynchronously(this.plugin, 2L);
   }

   public void saveStatsIntoFile(final Player var1, boolean var2) {
      if (this.joined) {
         if (!var2) {
            (new BukkitRunnable() {
               public void run() {
                  if (PlayerData.this.plugin.config.useMySQL) {
                     PlayerData.this.saveStatsInMySQL(var1);
                  } else {
                     PlayerData.this.saveStatsInPlayersFile(var1);
                  }

               }
            }).runTaskAsynchronously(this.plugin);
         } else if (this.plugin.config.useMySQL) {
            this.saveStatsInMySQL(var1);
         } else {
            this.saveStatsInPlayersFile(var1);
         }

      }
   }

   private void saveStatsInMySQL(Player var1) {
      String var2 = "NO_KITS";
      ArrayList var3 = new ArrayList();

      for(ItemStack var5 : this.kitsInventory.getAllContents()) {
         var3.add(ChatColor.stripColor(var5.getItemMeta().getDisplayName()));
      }

      if (!var3.isEmpty()) {
         var2 = (String)var3.get(0);

         for(int var7 = 1; var7 < var3.size(); ++var7) {
            var2 = var2 + ", " + (String)var3.get(var7);
         }
      }

      String var8 = this.plugin.config.UUID ? var1.getUniqueId().toString() : var1.getName();

      try {
         Statement var9 = this.plugin.mysql.getConnection().createStatement();
         if (var9.executeQuery("SELECT * FROM " + this.plugin.config.tableprefix + " WHERE " + (this.plugin.config.UUID ? "player_uuid" : "player_name") + " = '" + var8 + "';").next()) {
            this.plugin.mysql.getConnection().prepareStatement("UPDATE " + this.plugin.config.tableprefix + " SET player_uuid='" + var1.getUniqueId() + "', player_name='" + var1.getName() + "', Kits='" + var2 + "', Statistics='" + this.getStatisticsString(var1) + "' WHERE " + (this.plugin.config.UUID ? "player_uuid" : "player_name") + "='" + var8 + "';").executeUpdate();
         } else {
            var9.executeUpdate("INSERT INTO " + this.plugin.config.tableprefix + " (player_uuid, player_name, Kits, Statistics) VALUES ('" + var1.getUniqueId() + "', '" + var1.getName() + "', '" + var2 + "', '" + this.getStatisticsString(var1) + "')");
         }

         var9.close();
      } catch (SQLException var6) {
         var6.printStackTrace();
      }

   }

   private void saveStatsInPlayersFile(Player var1) {
      String var2 = this.plugin.config.UUID ? var1.getUniqueId().toString() : var1.getName();
      File var3 = new File(this.plugin.getDataFolder() + "/players/", var2);
      YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
      ((FileConfiguration)var4).set("Name", var1.getName());
      ((FileConfiguration)var4).set("Statistics", this.getStatisticsString(var1));
      ArrayList var5 = new ArrayList();

      for(ItemStack var7 : this.kitsInventory.getAllContents()) {
         var5.add(ChatColor.stripColor(var7.getItemMeta().getDisplayName()));
      }

      ((FileConfiguration)var4).set("Kits", var5);

      try {
         ((FileConfiguration)var4).save(var3);
      } catch (IOException var8) {
         var8.printStackTrace();
      }

   }

   private void loadStatisticsString(String var1) {
      String[] var2 = var1.split(":");
      this.kills = Integer.parseInt(var2[0]);
      this.deaths = Integer.parseInt(var2[1]);
      this.coins = Integer.parseInt(var2[2]);
      this.kitUnlockers = Integer.parseInt(var2[3]);
      this.dataexp = Integer.parseInt(var2[4]);
      this.projectiles_hit = Integer.parseInt(var2[5]);
      this.tournament_wins = Integer.parseInt(var2[6]);
      this.challenge_wins = Integer.parseInt(var2[7]);
      this.abilities_used = Integer.parseInt(var2[8]);
      this.soups_eaten = Integer.parseInt(var2[9]);
      this.killstreaks_earned = Integer.parseInt(var2[10]);
      this.loadSelectedTrail(Integer.parseInt(var2[11]));
      if (var2.length == 13) {
         this.elo = Integer.parseInt(var2[12]);
      } else {
         this.elo = this.plugin.config.StartingELO;
      }

   }

   public String getStatisticsString(Player var1) {
      return this.kills + ":" + this.deaths + ":" + this.coins + ":" + this.kitUnlockers + ":" + this.dataexp + ":" + this.projectiles_hit + ":" + this.tournament_wins + ":" + this.challenge_wins + ":" + this.abilities_used + ":" + this.soups_eaten + ":" + this.killstreaks_earned + ":" + this.getSelectedTrailSlot(var1) + ":" + this.elo;
   }

   private void loadSelectedTrail(int var1) {
      if (this.plugin.trailsInventory != null && var1 >= 0 && var1 < this.plugin.trailsInventory.getSize()) {
         ItemStack var2 = this.plugin.trailsInventory.getItem(var1);
         if (var2 != null && !var2.getType().equals(Material.AIR)) {
            this.selectedTrail = Effect.valueOf(ChatColor.stripColor(var2.getItemMeta().getDisplayName()));
         }
      }

   }

   private int getSelectedTrailSlot(Player var1) {
      if (var1 != null && this.selectedTrail != null && var1.hasPermission("kitbattle.trails")) {
         for(int var2 = 0; var2 < this.plugin.trailsInventory.getSize(); ++var2) {
            if (this.plugin.trailsInventory.getItem(var2) != null && ChatColor.stripColor(this.plugin.trailsInventory.getItem(var2).getItemMeta().getDisplayName()).equals(this.selectedTrail.name())) {
               return var2;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public void saveData(Player var1, PlayingMap var2) {
      this.joined = true;
      this.items = var1.getInventory().getContents();
      this.armor = var1.getInventory().getArmorContents();
      this.location = var1.getLocation();
      this.health = var1.getHealth();
      this.food = var1.getFoodLevel();
      this.levels = var1.getLevel();
      this.exp = var1.getExp();
      this.scoreboard = var1.getScoreboard();
      this.potions = var1.getActivePotionEffects();
      this.mode = var1.getGameMode();
      this.flying = var1.isFlying() || var1.getAllowFlight();
      this.setMap(var1, var2);
   }

   public void restoreData(Player var1) {
      var1.teleport(this.location);
      var1.getInventory().setContents(this.items);
      var1.getInventory().setArmorContents(this.armor);
      var1.setHealth(this.health);
      var1.setFoodLevel(this.food);
      var1.setLevel(this.levels);
      var1.setExp(this.exp);

      for(PotionEffect var3 : var1.getActivePotionEffects()) {
         var1.removePotionEffect(var3.getType());
      }

      var1.addPotionEffects(this.potions);
      var1.setGameMode(this.mode);
      if (this.flying) {
         var1.setAllowFlight(true);
         var1.setFlying(true);
      } else {
         var1.setAllowFlight(false);
         var1.setFlying(false);
      }

      var1.setScoreboard(this.scoreboard);
      this.destroyData(var1);
   }

   public void setMap(Player var1, PlayingMap var2) {
      if (this.map != null) {
         this.map.players.remove(var1.getUniqueId());
         this.map.updateSignPlayers();
      }

      this.map = var2;
      if (var2 != null) {
         var2.players.add(var1.getUniqueId());
         var2.updateSignPlayers();
      }

      if (this.customScoreboard != null) {
         this.customScoreboard.updatePlaceholder("%map%", this.getMap() != null ? this.getMap().name : "None");
      }

   }

   public PlayingMap getMap() {
      return this.map;
   }

   public void destroyData(Player var1) {
      this.items = null;
      this.armor = null;
      this.location = null;
      this.health = (double)0.0F;
      this.food = 0;
      this.levels = 0;
      this.exp = 0.0F;
      this.potions = null;
      this.mode = null;
      this.setMap(var1, (PlayingMap)null);
      this.kit = null;
      this.clearCooldowns();
      this.customScoreboard = null;
   }

   public int getKills() {
      return this.kills;
   }

   public void addKills(Player var1) {
      ++this.kills;
      this.plugin.achievementsManager.checkPlayer(var1, AchievementsManager.AchievementType.KILLS, this.kills);
   }

   public int getDeaths() {
      return this.deaths;
   }

   public void addDeaths() {
      ++this.deaths;
   }

   public int getProjectileHits() {
      return this.projectiles_hit;
   }

   public void addProjectileHits(Player var1) {
      ++this.projectiles_hit;
      this.plugin.achievementsManager.checkPlayer(var1, AchievementsManager.AchievementType.PROJECTILES_HIT, this.projectiles_hit);
   }

   public int getTournamentWins() {
      return this.tournament_wins;
   }

   public void addTournamentWins(Player var1) {
      ++this.tournament_wins;
      this.plugin.achievementsManager.checkPlayer(var1, AchievementsManager.AchievementType.TOURNAMENTS_WON, this.tournament_wins);
   }

   public int getChallengeWins() {
      return this.challenge_wins;
   }

   public void addChallengeWins(Player var1) {
      ++this.challenge_wins;
      this.plugin.achievementsManager.checkPlayer(var1, AchievementsManager.AchievementType.CHALLENGES_WON, this.challenge_wins);
   }

   public int getAbilitiesUsed() {
      return this.abilities_used;
   }

   public void addAbilitiesUsed(Player var1) {
      ++this.abilities_used;
      this.plugin.achievementsManager.checkPlayer(var1, AchievementsManager.AchievementType.ABILITIES_USED, this.abilities_used);
   }

   public int getSoupsEaten() {
      return this.soups_eaten;
   }

   public void addSoupsEaten(Player var1) {
      ++this.soups_eaten;
      this.plugin.achievementsManager.checkPlayer(var1, AchievementsManager.AchievementType.SOUPS_EATEN, this.soups_eaten);
   }

   public int getKillstreaksEarned() {
      return this.killstreaks_earned;
   }

   public void addKillstreaksEarned(Player var1) {
      ++this.killstreaks_earned;
      this.plugin.achievementsManager.checkPlayer(var1, AchievementsManager.AchievementType.KILLSTREAKS_EARNED, this.killstreaks_earned);
   }

   public Kit getKit() {
      return this.kit;
   }

   public void setKit(Player var1, Kit var2) {
      this.kit = var2;
      if (this.customScoreboard != null) {
         this.customScoreboard.updatePlaceholder("%selected_kit%", this.getKit() != null ? this.getKit().getName() : "None");
      }

   }

   public boolean addExp(Player var1, int var2) {
      Rank var3 = this.getRank();
      this.setExp(var1, this.dataexp + var2);
      Rank var4 = this.getRank();
      return var3 != var4 && var4.getRequiredExp() > var3.getRequiredExp();
   }

   public int getExp() {
      return this.dataexp;
   }

   public void setExp(Player var1, int var2) {
      if (this.dataexp != var2) {
         int var3 = this.dataexp;
         this.dataexp = var2;
         if (this.dataexp <= var3 || this.getNextRank() != null && this.dataexp >= this.getNextRank().getRequiredExp()) {
            Rank var4 = this.getRank();
            this.updateRank();
            Rank var5 = this.getRank();
            if (!var5.getName().equals(var4.getName())) {
               if (var5.getRequiredExp() > var4.getRequiredExp()) {
                  ArrayList var6 = new ArrayList();

                  for(Rank var8 : this.plugin.Ranks.values()) {
                     if (var8.getRequiredExp() > var4.getRequiredExp() && var8.getRequiredExp() < var5.getRequiredExp()) {
                        var6.add(var8);
                     }
                  }

                  var6.add(var5);

                  for(Rank var12 : var6) {
                     if (this.plugin.config.BroadcastRankUp) {
                        Bukkit.broadcastMessage(((String)this.plugin.msgs.messages.get("Player-Rank-Up-Public-Message")).replace("%player%", var1.getName()).replace("%rank%", var12.getName()));
                     } else {
                        for(Player var10 : Utils.getPlayers(this.plugin.players)) {
                           var10.sendMessage(((String)this.plugin.msgs.messages.get("Player-Rank-Up-Public-Message")).replace("%player%", var1.getName()).replace("%rank%", var12.getName()));
                        }
                     }

                     for(String var14 : var12.getExcutedCommands()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var14.replaceAll("%player%", var1.getName()));
                     }
                  }
               } else {
                  Bukkit.broadcastMessage(((String)this.plugin.msgs.messages.get("Player-Derank-Public-Message")).replace("%player%", var1.getName()).replace("%rank%", var5.getName()));
               }
            }

         }
      }
   }

   public Rank getRank() {
      return this.rank;
   }

   public void setRank(Rank var1) {
      this.rank = var1;
   }

   public Rank getNextRank() {
      return this.nextRank;
   }

   public int getKillstreak() {
      return this.killstreak;
   }

   public int getDeathstreak() {
      return this.deathstreak;
   }

   public int getKitUnlockers() {
      return this.kitUnlockers;
   }

   public int getELO() {
      return this.elo;
   }

   public void addELO(int var1) {
      this.elo += var1;
   }

   public void removeELO(int var1) {
      this.elo -= var1;
   }

   public int getCoins(Player var1) {
      return this.plugin.econ != null ? (int)this.plugin.econ.getBalance(var1) : this.coins;
   }

   public void addCoins(Player var1, int var2) {
      if (this.plugin.econ != null) {
         this.plugin.econ.depositPlayer(var1, (double)var2);
      } else {
         this.coins += var2;
         if (!this.joined) {
            this.joined = true;
         }
      }

   }

   public void removeCoins(Player var1, int var2) {
      if (this.plugin.econ != null) {
         this.plugin.econ.withdrawPlayer(var1, (double)var2);
      } else {
         this.coins -= var2;
         if (!this.joined) {
            this.joined = true;
         }
      }

   }

   public void setCoins(Player var1, int var2) {
      if (this.plugin.econ != null) {
         int var3 = var2 - this.getCoins(var1);
         if (var3 > 0) {
            this.plugin.econ.depositPlayer(var1, (double)var3);
         } else {
            this.plugin.econ.withdrawPlayer(var1, (double)(-var3));
         }
      } else {
         this.coins = var2;
         if (!this.joined) {
            this.joined = true;
         }
      }

   }

   public void updateRank() {
      for(int var1 = 0; var1 < Rank.orderd.size(); ++var1) {
         Rank var2 = (Rank)Rank.orderd.get(var1);
         if (this.dataexp >= var2.getRequiredExp()) {
            this.rank = var2;
            this.nextRank = Rank.orderd.size() <= var1 + 1 ? null : (Rank)Rank.orderd.get(var1 + 1);
         }
      }

   }

   public void createScoreboard(Player var1) {
      if (this.plugin.config.ScoreboardEnabled) {
         this.customScoreboard = new CustomScoreboard(this.plugin, this.plugin.config.showHealthBelowName, this.plugin.msgs.scoreboard_title, this.plugin.msgs.defaultScoreboard);
         this.customScoreboard.updatePlaceholder("%kills%", this.getKills());
         this.customScoreboard.updatePlaceholder("%deaths%", this.getDeaths());
         this.customScoreboard.updatePlaceholder("%coins%", this.getCoins(var1));
         this.customScoreboard.updatePlaceholder("%killstreak%", this.getKillstreak());
         this.customScoreboard.updatePlaceholder("%deathstreak%", this.getDeathstreak());
         this.customScoreboard.updatePlaceholder("%player_exp%", this.getExp());
         this.customScoreboard.updatePlaceholder("%player_rank%", this.getRank() != null ? this.getRank().getName() : "None");
         this.customScoreboard.updatePlaceholder("%player_next_rank%", this.getNextRank() != null ? this.getNextRank().getName() : "None");
         this.customScoreboard.updatePlaceholder("%player_next_rank_exp%", this.getNextRank() != null ? String.valueOf(this.getNextRank().getRequiredExp()) : "0");
         this.customScoreboard.updatePlaceholder("%player_next_rank_exp_difference%", this.getNextRank() != null ? String.valueOf(this.getNextRank().getRequiredExp() - this.getExp()) : "0");
         this.customScoreboard.updatePlaceholder("%elo%", String.valueOf(this.elo));
         this.customScoreboard.updatePlaceholder("%map%", this.getMap() != null ? this.getMap().name : "None");
         this.customScoreboard.updatePlaceholder("%selected_kit%", this.getKit() != null ? this.getKit().getName() : "None");
         this.customScoreboard.apply(var1);
      }
   }

   public CustomScoreboard getScoreboard() {
      return this.customScoreboard;
   }

   public void addDamage(Player var1, double var2) {
      this.damagers.put(var1.getName(), this.damagers.containsKey(var1.getName()) ? (Double)this.damagers.get(var1.getName()) + var2 : var2);
   }

   public boolean hasKitSelectionCooldown(Player var1, String var2) {
      String var3 = "KitSelectionCooldown-" + var2;
      long var4 = this.kitSelectionCooldown.containsKey(var3) ? (Long)this.kitSelectionCooldown.get(var3) - System.currentTimeMillis() : 0L;
      if (var4 > 0L) {
         var1.sendMessage(((String)this.plugin.msgs.messages.get("Kit-Selection-Cooldown")).replace("%time%", String.valueOf((new BigDecimal(Double.valueOf((double)var4) / (double)1000.0F)).setScale(1, RoundingMode.HALF_UP).doubleValue())));
         return true;
      } else {
         return false;
      }
   }

   public void setKitSelectionCooldown(String var1, int var2) {
      String var3 = "KitSelectionCooldown-" + var1;
      this.kitSelectionCooldown.put(var3, System.currentTimeMillis() + (long)(var2 * 1000));
   }

   public boolean hasCooldown(Player var1, String var2) {
      long var3 = this.cooldowns.containsKey(var2) ? (Long)this.cooldowns.get(var2) - System.currentTimeMillis() : 0L;
      if (var3 > 0L) {
         var1.sendMessage(((String)this.plugin.msgs.messages.get("Still-On-Cooldown")).replace("%time%", String.valueOf((new BigDecimal(Double.valueOf((double)var3) / (double)1000.0F)).setScale(1, RoundingMode.HALF_UP).doubleValue())));
         return true;
      } else {
         return false;
      }
   }

   public String getAbilityCooldown(Player var1) {
      if (this.lastAbilityKey != null) {
         long var2 = (Long)this.cooldowns.get(this.lastAbilityKey) - System.currentTimeMillis();
         return String.valueOf(Math.max((new BigDecimal(Double.valueOf((double)var2) / (double)1000.0F)).setScale(1, RoundingMode.HALF_UP).doubleValue(), (double)0.0F));
      } else {
         return "0";
      }
   }

   public void clearCooldowns() {
      this.cooldowns.clear();
      this.lastAbilityKey = null;
      if (this.abilityCooldownNotifier != null) {
         this.abilityCooldownNotifier.cancel();
         this.abilityCooldownNotifier = null;
      }

   }

   public void setCooldown(final Player var1, String var2, final int var3, boolean var4) {
      this.cooldowns.put(var2, System.currentTimeMillis() + (long)(var3 * 1000));
      if (var4 && this.plugin.config.NotifyWhenCooldownOff) {
         this.lastAbilityKey = var2;
         if (this.plugin.config.abilityActionbarCooldown && TitleManager.getInstance().isActionBarEnabled()) {
            if (this.abilityCooldownNotifier != null) {
               this.abilityCooldownNotifier.cancel();
            }

            this.abilityCooldownNotifier = (new BukkitRunnable() {
               double counter = (double)var3;

               public void run() {
                  this.counter -= PlayerData.this.plugin.config.actionBarDecrement;
                  int var1x = (int)((double)20.0F * ((double)var3 - this.counter) / (double)var3);
                  String var2 = PlayerData.this.plugin.getProgressBar(var1x) + ChatColor.WHITE + " " + PlayerData.this.plugin.config.actionBarTimeFormat.format(this.counter) + "s";
                  TitleManager.getInstance().sendActionBar(var1, var2);
                  if (this.counter <= (double)0.0F) {
                     TitleManager.getInstance().sendActionBar(var1, "Ability ready!");
                     this.cancel();
                     PlayerData.this.abilityCooldownNotifier = null;
                  }

               }
            }).runTaskTimerAsynchronously(this.plugin, (long)this.plugin.config.actionBarUpdateSpeed, (long)this.plugin.config.actionBarUpdateSpeed);
         } else {
            this.abilityCooldownNotifier = (new BukkitRunnable() {
               public void run() {
                  if (var1 != null && PlayerData.this.plugin.players.contains(var1.getUniqueId())) {
                     var1.sendMessage((String)PlayerData.this.plugin.msgs.messages.get("Cooldown-Remove"));
                     PlayerData.this.abilityCooldownNotifier = null;
                  }

               }
            }).runTaskLater(this.plugin, (long)(var3 * 20));
         }
      }

   }

   public int getTotalBounty() {
      int var1 = 0;

      for(int var3 : this.bounties.values()) {
         var1 += var3;
      }

      return var1;
   }

   public void cancelBounty(String var1) {
      if (!this.bounties.isEmpty()) {
         for(String var3 : this.bounties.keySet()) {
            Player var4 = Bukkit.getPlayer(var3);
            if (var4 != null) {
               int var5 = (Integer)this.bounties.get(var3);
               PlayerDataManager.get(var4).addCoins(var4, var5);
               var4.sendMessage(((String)this.plugin.msgs.messages.get("Bounty-Cancel")).replace("%player%", var1).replace("%bounty%", String.valueOf(var5)));
            }
         }

         this.bounties.clear();
      }
   }

   public void resetPlayer(Player var1) {
      this.coins = this.plugin.config.StartingCoins;
      this.kills = 0;
      this.deaths = 0;
      this.dataexp = 0;
      this.projectiles_hit = 0;
      this.tournament_wins = 0;
      this.challenge_wins = 0;
      this.abilities_used = 0;
      this.soups_eaten = 0;
      this.killstreaks_earned = 0;
      this.updateRank();
      this.achievements = this.plugin.achievementsManager.getAchievements(this);
      this.kitUnlockers = this.plugin.config.StartingAmountOfKitUnlockers;
      this.elo = this.plugin.config.StartingELO;
      this.loadKits(new ArrayList());
   }

   public void loadKits(List<String> var1) {
      this.kitsInventory = new SmartInventory(this.plugin, (String)this.plugin.msgs.inventories.get("Kits"));
      if (var1.isEmpty()) {
         var1.addAll(this.plugin.config.defaultKits);
      }

      Iterator var2 = var1.iterator();

      for(int var3 = 0; (double)var3 < Math.ceil((double)var1.size() / (double)SmartInventory.smartSlots.length); ++var3) {
         if (var3 >= this.kitsInventory.getSize()) {
            this.kitsInventory.addInventory(ChatColor.RED + "List #" + (var3 + 1));
         }

         for(int var7 : SmartInventory.smartSlots) {
            if (!var2.hasNext()) {
               break;
            }

            String var8 = (String)var2.next();
            if (this.plugin.Kits.containsKey(var8)) {
               this.kitsInventory.setItem(var3, var7, ((Kit)this.plugin.Kits.get(var8)).getLogo());
            }
         }
      }

   }

   public void openUpgrades(Player var1) {
      ArrayList var2 = new ArrayList();

      for(ItemStack var4 : this.kitsInventory.getAllContents()) {
         if (!var4.getType().equals(Material.AIR)) {
            String var5 = ChatColor.stripColor(var4.getItemMeta().getDisplayName()).toLowerCase();

            for(Kit var7 : this.plugin.Kits.values()) {
               if (var7.original != null && var7.original.name.toLowerCase().equals(var5)) {
                  var2.add(var7);
               }
            }
         }
      }

      if (var2.isEmpty()) {
         var1.sendMessage((String)this.plugin.msgs.messages.get("No-Upgrades"));
         var1.closeInventory();
      } else {
         this.upgradesInventory = new SmartInventory(this.plugin, (String)this.plugin.msgs.inventories.get("Shop"));
         Iterator var10 = var2.iterator();

         for(int var11 = 0; (double)var11 < Math.ceil((double)var2.size() / (double)SmartInventory.smartSlots.length); ++var11) {
            if (var11 >= this.upgradesInventory.getSize()) {
               this.upgradesInventory.addInventory(ChatColor.DARK_PURPLE + "Upgrades");
               this.upgradesInventory.setItem(var11, 53, this.plugin.back_itemstack);
            }

            for(int var8 : SmartInventory.smartSlots) {
               if (!var10.hasNext()) {
                  break;
               }

               Kit var9 = (Kit)var10.next();
               this.upgradesInventory.setItem(var11, var8, var9.getShopLogo());
            }
         }

         this.upgradesInventory.open(var1);
      }
   }

   public int getCombatLogDurationInSeconds() {
      int var1 = 0;
      if ((System.currentTimeMillis() - this.lastHitTime) / 1000L <= (long)this.plugin.config.combatLogDuration) {
         var1 = (int)((double)this.plugin.config.combatLogDuration - (double)(System.currentTimeMillis() - this.lastHitTime) / (double)1000.0F);
      }

      return var1;
   }

   public Inventory getStatsInventory(Player var1) {
      Inventory var2 = Bukkit.createInventory((InventoryHolder)null, 36, (String)this.plugin.msgs.inventories.get("Stats-Inventory"));
      this.plugin.cageInventory(var2, false);
      var2.setItem(var2.getSize() - 5, this.plugin.back_itemstack);
      double var3 = (new BigDecimal(this.deaths > 1 ? Double.valueOf((double)this.kills) / (double)this.deaths : (double)this.kills)).setScale(2, RoundingMode.HALF_UP).doubleValue();
      ItemStackBuilder var5 = new ItemStackBuilder(Material.PAPER);
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + (String)this.plugin.msgs.stats.get("KILLS") + ":").addLore(ChatColor.YELLOW + String.valueOf(this.kills)).setType(Material.IRON_SWORD).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + (String)this.plugin.msgs.stats.get("DEATHS") + ":").addLore(ChatColor.YELLOW + String.valueOf(this.deaths)).setType(Material.REDSTONE).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + (String)this.plugin.msgs.stats.get("KDR") + ":").addLore(ChatColor.YELLOW + String.valueOf(var3)).setType(Material.ENCHANTED_BOOK).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + (String)this.plugin.msgs.stats.get("COINS") + ":").addLore(ChatColor.YELLOW + String.valueOf(this.getCoins(var1))).setType(Material.EMERALD).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + (String)this.plugin.msgs.stats.get("TOURNAMENT_WINS") + ":").addLore(ChatColor.YELLOW + String.valueOf(this.tournament_wins)).setType(Material.DIAMOND).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + (String)this.plugin.msgs.stats.get("CHALLENGE_WINS") + ":").addLore(ChatColor.YELLOW + String.valueOf(this.challenge_wins)).setType(Material.GOLD_INGOT).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + (String)this.plugin.msgs.stats.get("PROJECTILES_HIT") + ":").addLore(ChatColor.YELLOW + String.valueOf(this.projectiles_hit)).setType(Material.BOW).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + (String)this.plugin.msgs.stats.get("EXP") + ":").addLore(ChatColor.YELLOW + String.valueOf(this.dataexp)).setType(XMaterial.EXPERIENCE_BOTTLE.parseMaterial()).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + (String)this.plugin.msgs.stats.get("RANK") + ":").addLore(ChatColor.YELLOW + this.rank.getName()).setType(Material.CHEST).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + (String)this.plugin.msgs.stats.get("ABILITIES_USED") + ":").addLore(ChatColor.YELLOW + String.valueOf(this.abilities_used)).setType(XMaterial.REDSTONE_TORCH.parseMaterial()).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + (String)this.plugin.msgs.stats.get("SOUPS_EATEN") + ":").addLore(ChatColor.YELLOW + String.valueOf(this.soups_eaten)).setType(XMaterial.MUSHROOM_STEW.parseMaterial()).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + (String)this.plugin.msgs.stats.get("KILLSTREAKS_EARNED") + ":").addLore(ChatColor.YELLOW + String.valueOf(this.killstreaks_earned)).setType(Material.DIAMOND_AXE).build()});
      var2.addItem(new ItemStack[]{var5.setName(ChatColor.GREEN + (String)this.plugin.msgs.stats.get("ELO") + ":").addLore(ChatColor.YELLOW + String.valueOf(this.elo)).setType(Material.NETHER_STAR).build()});
      return var2;
   }

   public void sendStats(CommandSender var1, Player var2) {
      var1.sendMessage(this.plugin.msgs.statsCommandHeader.replace("%player%", var2.getName()));
      var1.sendMessage(this.plugin.msgs.statsCommandBody.replace("%stat%", (CharSequence)this.plugin.msgs.stats.get("KILLS")).replace("%score%", String.valueOf(this.kills)));
      var1.sendMessage(this.plugin.msgs.statsCommandBody.replace("%stat%", (CharSequence)this.plugin.msgs.stats.get("DEATHS")).replace("%score%", String.valueOf(this.deaths)));
      var1.sendMessage(this.plugin.msgs.statsCommandBody.replace("%stat%", (CharSequence)this.plugin.msgs.stats.get("KDR")).replace("%score%", String.valueOf((new BigDecimal(this.deaths > 1 ? Double.valueOf((double)this.kills) / (double)this.deaths : (double)this.kills)).setScale(2, RoundingMode.HALF_UP).doubleValue())));
      var1.sendMessage(this.plugin.msgs.statsCommandBody.replace("%stat%", (CharSequence)this.plugin.msgs.stats.get("COINS")).replace("%score%", String.valueOf(this.getCoins(var2))));
      var1.sendMessage(this.plugin.msgs.statsCommandBody.replace("%stat%", (CharSequence)this.plugin.msgs.stats.get("TOURNAMENT_WINS")).replace("%score%", String.valueOf(this.tournament_wins)));
      var1.sendMessage(this.plugin.msgs.statsCommandBody.replace("%stat%", (CharSequence)this.plugin.msgs.stats.get("CHALLENGE_WINS")).replace("%score%", String.valueOf(this.challenge_wins)));
      var1.sendMessage(this.plugin.msgs.statsCommandBody.replace("%stat%", (CharSequence)this.plugin.msgs.stats.get("PROJECTILES_HIT")).replace("%score%", String.valueOf(this.projectiles_hit)));
      var1.sendMessage(this.plugin.msgs.statsCommandBody.replace("%stat%", (CharSequence)this.plugin.msgs.stats.get("EXP")).replace("%score%", String.valueOf(this.dataexp)));
      var1.sendMessage(this.plugin.msgs.statsCommandBody.replace("%stat%", (CharSequence)this.plugin.msgs.stats.get("RANK")).replace("%score%", String.valueOf(this.rank.getName())));
      var1.sendMessage(this.plugin.msgs.statsCommandBody.replace("%stat%", (CharSequence)this.plugin.msgs.stats.get("ABILITIES_USED")).replace("%score%", String.valueOf(this.abilities_used)));
      var1.sendMessage(this.plugin.msgs.statsCommandBody.replace("%stat%", (CharSequence)this.plugin.msgs.stats.get("SOUPS_EATEN")).replace("%score%", String.valueOf(this.soups_eaten)));
      var1.sendMessage(this.plugin.msgs.statsCommandBody.replace("%stat%", (CharSequence)this.plugin.msgs.stats.get("KILLSTREAKS_EARNED")).replace("%score%", String.valueOf(this.killstreaks_earned)));
      var1.sendMessage(this.plugin.msgs.statsCommandBody.replace("%stat%", (CharSequence)this.plugin.msgs.stats.get("ELO")).replace("%score%", String.valueOf(this.elo)));
      var1.sendMessage(this.plugin.msgs.statsCommandFooter);
   }
}
