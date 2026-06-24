package me.wazup.kitbattle;

import com.shampaggon.crackshot.CSUtility;
import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.storage.ServerStorage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import me.wazup.kitbattle.abilities.AbilityManager;
import me.wazup.kitbattle.commands.MainCommand;
import me.wazup.kitbattle.custommaps.CombinedMapRenderer;
import me.wazup.kitbattle.custommaps.CustomMapsManager;
import me.wazup.kitbattle.holograms.GeneralizedHologramsManager;
import me.wazup.kitbattle.listeners.GUIListener;
import me.wazup.kitbattle.listeners.KBListener;
import me.wazup.kitbattle.managers.AchievementsManager;
import me.wazup.kitbattle.managers.FileManager;
import me.wazup.kitbattle.managers.NotificationsManager;
import me.wazup.kitbattle.managers.PlayerDataManager;
import me.wazup.kitbattle.managers.SoundsManager;
import me.wazup.kitbattle.managers.TitleManager;
import me.wazup.kitbattle.placeholderhooks.MVdWPlacholderHook;
import me.wazup.kitbattle.placeholderhooks.PlaceholderAPIHooks;
import me.wazup.kitbattle.utils.Cuboid;
import me.wazup.kitbattle.utils.ItemStackBuilder;
import me.wazup.kitbattle.utils.Metrics;
import me.wazup.kitbattle.utils.PlayerUtils;
import me.wazup.kitbattle.utils.SmartInventory;
import me.wazup.kitbattle.utils.Utils;
import me.wazup.kitbattle.utils.XMaterial;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Kitbattle extends JavaPlugin {
   private static Kitbattle instance;
   public HashMap<String, PlayingMap> playingMaps = new HashMap();
   public HashMap<String, TournamentMap> tournamentMaps = new HashMap();
   public HashMap<String, ChallengeMap> challengeMaps = new HashMap();
   public HashMap<String, Kit> Kits = new HashMap();
   public HashMap<String, Rank> Ranks = new HashMap();
   public ArrayList<Entity> toRemove = new ArrayList();
   public ArrayList<BlockState> toRollback = new ArrayList();
   public ArrayList<UUID> players = new ArrayList();
   public ItemStack wand_itemstack;
   public ItemStack pane_itemstack;
   public ItemStack yellow_pane_itemstack;
   public ItemStack confirm_itemstack;
   public ItemStack cancel_itemstack;
   public ItemStack next_itemstack;
   public ItemStack previous_itemstack;
   public ItemStack back_itemstack;
   public TournamentManager tournamentsManager;
   public ChallengesManager challengesManager;
   public KBListener listen;
   public Config config;
   public FileManager fileManager;
   public Messages msgs;
   public Economy econ = null;
   public boolean QualityArmoryEnabled;
   public boolean AdvancedEnchantmentsEnabled;
   public boolean ItemsAdderEnabled;
   public CSUtility crackShotAPI;
   public ServerStorage itemEdit;
   public HashMap<UUID, Location[]> selectionMode = new HashMap();
   public HashMap<Location, Integer> topSigns;
   public ArrayList<UUID> editmode = new ArrayList();
   public ArrayList<UUID> spectating = new ArrayList();
   public CustomizableScrollableInventory shop;
   public Inventory profileInventory;
   public Inventory trailsInventory;
   public BungeeMode bungeeMode;
   public AchievementsManager achievementsManager;
   public MySQL mysql;
   public GeneralizedHologramsManager hologramsManager;
   CustomMapsManager customMapsManager;
   BukkitTask scoreboardTitleAnimationTask;
   BukkitTask savingTask;
   public BukkitTask leaderboard_updater;
   public long leaderboard_updater_time = 0L;
   String character_heart;
   String character_radioactive;
   String character_right_arrow;
   String character_left_arrow;
   public boolean availableUpdate;
   String progressBar;

   public static Kitbattle getInstance() {
      return instance;
   }

   public void onEnable() {
      loadConfig0();
      instance = this;
      MainCommand var1 = new MainCommand();
      this.getCommand("kitbattle").setExecutor(var1);
      this.getCommand("kitbattle").setTabCompleter(var1);
      this.config = new Config();
      this.msgs = new Messages();
      new AbilityManager();
      if (this.getConfig().getBoolean("Check-For-General-Notifications")) {
         new NotificationsManager();
      }

      this.customMapsManager = new CustomMapsManager();
      SoundsManager.loadSounds();
      ItemStackBuilder.loadMethods();
      this.character_heart = StringEscapeUtils.unescapeJava("❤");
      this.character_radioactive = StringEscapeUtils.unescapeJava("☢");
      this.character_right_arrow = StringEscapeUtils.unescapeJava("⤇");
      this.character_left_arrow = StringEscapeUtils.unescapeJava("⤆");
      this.progressBar = "";
      String var2 = StringEscapeUtils.unescapeJava("▌");

      for(int var3 = 0; var3 < 20; ++var3) {
         this.progressBar = this.progressBar + var2;
      }

      this.listen = new KBListener();
      Bukkit.getPluginManager().registerEvents(this.listen, this);
      Bukkit.getPluginManager().registerEvents(new GUIListener(), this);
      this.wand_itemstack = (new ItemStackBuilder(Material.BLAZE_ROD)).setName(ChatColor.AQUA + "KitBattle" + ChatColor.LIGHT_PURPLE + " Wand").addLore(ChatColor.YELLOW + "--------------------------", ChatColor.GREEN + "Left click to select the first corner", ChatColor.GREEN + "Right click to select the second corner", ChatColor.YELLOW + "--------------------------").build();
      this.pane_itemstack = (new ItemStackBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem())).setName(" ").build();
      this.yellow_pane_itemstack = (new ItemStackBuilder(XMaterial.YELLOW_STAINED_GLASS.parseItem())).setName(" ").build();
      this.confirm_itemstack = (new ItemStackBuilder(XMaterial.LIME_STAINED_GLASS.parseItem())).setName(ChatColor.GREEN + "Confirm").build();
      this.cancel_itemstack = (new ItemStackBuilder(XMaterial.RED_STAINED_GLASS.parseItem())).setName(ChatColor.RED + "Cancel").build();
      this.next_itemstack = (new ItemStackBuilder(XMaterial.LIME_STAINED_GLASS.parseItem())).setName(ChatColor.GREEN + "Next page " + this.character_right_arrow).build();
      this.previous_itemstack = (new ItemStackBuilder(XMaterial.RED_STAINED_GLASS.parseItem())).setName(ChatColor.RED + this.character_left_arrow + " Previous page").build();
      this.back_itemstack = (new ItemStackBuilder(Material.ARROW)).setName(ChatColor.GREEN + "Back").build();
      if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
         Bukkit.getConsoleSender().sendMessage("[KitBattle] Found PlaceholderAPI, Hooked: " + (new PlaceholderAPIHooks(this)).register());
      }

      if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
         new MVdWPlacholderHook(this);
         Bukkit.getConsoleSender().sendMessage("[KitBattle] Found MVdWPlaceholderAPI, Hooked: true");
      }

      if (Bukkit.getPluginManager().isPluginEnabled("ItemEdit")) {
         this.itemEdit = ItemEdit.get().getServerStorage();
      }

      this.AdvancedEnchantmentsEnabled = Bukkit.getPluginManager().isPluginEnabled("AdvancedEnchantments");
      this.QualityArmoryEnabled = Bukkit.getPluginManager().isPluginEnabled("QualityArmory");
      this.ItemsAdderEnabled = Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
      if (Bukkit.getPluginManager().isPluginEnabled("CrackShot")) {
         this.crackShotAPI = new CSUtility();
      }

      this.convertPlayerFiles();
      this.setupAll();
      PlayerDataManager.loadAll();
      this.setupMetrics();
      Bukkit.getConsoleSender().sendMessage("[KitBattle] has been enabled!");
   }

   public void onDisable() {
      for(Player var2 : Utils.getPlayers(this.players)) {
         var2.sendMessage((String)this.msgs.messages.get("Plugin-Reload-Kick-Message"));
         PlayerDataManager.get(var2).restoreData(var2);
      }

      for(Player var7 : Utils.getPlayers(this.selectionMode.keySet())) {
         var7.getInventory().removeItem(new ItemStack[]{this.wand_itemstack});
      }

      for(Entity var8 : this.toRemove) {
         var8.remove();
      }

      for(Player var9 : Utils.getOnlinePlayers()) {
         PlayerDataManager.get(var9).saveStatsIntoFile(var9, true);
      }

      for(BlockState var10 : this.toRollback) {
         Utils.Rollback(var10);
      }

      if (this.hologramsManager != null && this.hologramsManager.leaderboardHologram != null) {
         this.hologramsManager.leaderboardHologram.delete();
      }

      Bukkit.getConsoleSender().sendMessage("[KitBattle] has been disabled!");
   }

   public void setupAll() {
      this.fileManager = new FileManager(this);
      this.fileManager.setupKits();
      this.msgs.loadMessages(this.fileManager.getConfig("messages.yml"));
      this.config.loadConfig();
      this.Kits.clear();
      this.fileManager.loadKits();
      this.fileManager.setupRanks();
      this.Ranks.clear();
      this.fileManager.loadRanks();
      AbilityManager.getInstance().loadAbilitiesConfig();
      new TitleManager(this, this.getConfig().getBoolean("Titles-Enabled"), this.getConfig().getBoolean("Action-Bar.enabled"));
      this.createDefaultAchievements();
      this.achievementsManager = new AchievementsManager(this);
      this.econ = null;
      if (this.getConfig().getBoolean("use-Vault")) {
         this.setupEcon();
         if (this.econ != null) {
            Bukkit.getConsoleSender().sendMessage("[KitBattle] Found vault! The option to use vault is enabled, due to that the plugin will use vault instead of coins");
         } else {
            Bukkit.getConsoleSender().sendMessage("[KitBattle] The option to use vault is enabled, but Vault doesn't seem to be installed! due to that the plugin will continue using coins system");
         }
      }

      if (this.mysql != null) {
         try {
            this.mysql.getConnection().close();
            this.mysql = null;
         } catch (SQLException var9) {
            var9.printStackTrace();
         }
      }

      if (this.config.useMySQL) {
         try {
            this.mysql = new MySQL(this.config.tableprefix, this.config.mysqlhost, this.config.mysqlport, this.config.mysqldatabase, this.config.mysqlusername, this.config.mysqlpassword);
            this.mysql.setupTable();
         } catch (SQLException var8) {
            var8.printStackTrace();
         }
      }

      this.profileInventory = Bukkit.createInventory((InventoryHolder)null, 9, (String)this.msgs.inventories.get("Profile-Inventory"));
      this.cageInventory(this.profileInventory, true);
      this.profileInventory.setItem(2, (new ItemStackBuilder(Material.PAPER)).setName(ChatColor.LIGHT_PURPLE + "Stats").build());
      this.profileInventory.setItem(6, (new ItemStackBuilder(Material.ENDER_CHEST)).setName(ChatColor.LIGHT_PURPLE + "Achievements").build());
      this.trailsInventory = null;
      if (this.config.TrailsEnabled) {
         this.trailsInventory = Bukkit.createInventory((InventoryHolder)null, 54, (String)this.msgs.inventories.get("Trails-Inventory"));
         List var1 = this.fileManager.getConfig("trails_blacklist.yml").getStringList("Blacklisted-Trails");

         for(Effect var5 : Effect.values()) {
            if (!var1.contains(var5.name())) {
               this.trailsInventory.addItem(new ItemStack[]{(new ItemStackBuilder(Material.ENCHANTED_BOOK)).setName(Utils.getRandomColor() + var5.name()).build()});
            }
         }

         this.trailsInventory.setItem(this.trailsInventory.getSize() - 1, (new ItemStackBuilder(Material.ARROW)).setName(ChatColor.RED + "NONE").build());
      }

      this.loadMaps();
      this.topSigns = new HashMap();
      final FileConfiguration var10 = this.fileManager.getConfig("signs.yml");
      (new BukkitRunnable() {
         public void run() {
            if (var10.getConfigurationSection("Signs.Top") != null && !var10.getConfigurationSection("Signs.Top").getKeys(false).isEmpty()) {
               for(String var2 : var10.getConfigurationSection("Signs.Top").getKeys(false)) {
                  int var3 = Integer.valueOf(var2);
                  World var4 = Bukkit.getWorld(var10.getString("Signs.Top." + var3 + ".world"));
                  int var5 = var10.getInt("Signs.Top." + var2 + ".x");
                  int var6 = var10.getInt("Signs.Top." + var2 + ".y");
                  int var7 = var10.getInt("Signs.Top." + var2 + ".z");
                  Kitbattle.this.topSigns.put(new Location(var4, (double)var5, (double)var6, (double)var7), var3);
               }
            }

            if (Kitbattle.this.hologramsManager != null && Kitbattle.this.hologramsManager.leaderboardHologram != null) {
               Kitbattle.this.hologramsManager.leaderboardHologram.delete();
            }

            Kitbattle.this.hologramsManager = null;
            if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays") || Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
               GeneralizedHologramsManager.HologramProvider var8;
               if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
                  var8 = GeneralizedHologramsManager.HologramProvider.HolographicDisplays;
                  Bukkit.getConsoleSender().sendMessage(Messages.getInstance().prefix + "HolographicDisplays has been detected! HolographicDisplays features are now accessible..");
               } else {
                  var8 = GeneralizedHologramsManager.HologramProvider.DecentHolograms;
                  Bukkit.getConsoleSender().sendMessage(Messages.getInstance().prefix + "DecentHolograms has been detected! DecentHolograms features are now accessible..");
               }

               Kitbattle.this.hologramsManager = new GeneralizedHologramsManager(var8);
               if (Kitbattle.this.getConfig().contains("Holographic-Leaderboard")) {
                  World var9 = Bukkit.getWorld(Kitbattle.this.getConfig().getString("Holographic-Leaderboard.world"));
                  int var10x = Kitbattle.this.getConfig().getInt("Holographic-Leaderboard.x");
                  int var11 = Kitbattle.this.getConfig().getInt("Holographic-Leaderboard.y");
                  int var12 = Kitbattle.this.getConfig().getInt("Holographic-Leaderboard.z");
                  Kitbattle.this.hologramsManager.setLeaderboardLocation(new Location(var9, (double)var10x, (double)var11, (double)var12), false);
               }
            }

            Kitbattle.this.startLeaderboardUpdater();
         }
      }).runTaskLater(this, 10L);
      FileConfiguration var11 = this.fileManager.getConfig("shop.yml");
      if (var11.getBoolean("Create-Default-Shop")) {
         var11.addDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(this.getResource("shop-default.yml"))));
         var11.options().copyDefaults(true);
         var11.set("Create-Default-Shop", false);
         this.fileManager.saveConfig("shop.yml");
      }

      this.shop = new CustomizableScrollableInventory((String)this.msgs.inventories.get("Shop"), var11) {
         ItemStack getItem(String var1) {
            return Kitbattle.this.Kits.containsKey(var1.toLowerCase()) ? ((Kit)Kitbattle.this.Kits.get(var1.toLowerCase())).getShopLogo() : null;
         }
      };
      if (!this.players.isEmpty() && this.config.scoreboardTitleAnimationEnabled && this.scoreboardTitleAnimationTask == null) {
         this.startTitleAnimation();
      }

      if (this.savingTask != null) {
         this.savingTask.cancel();
      }

      if (this.getConfig().getBoolean("Saving-Task.Enabled")) {
         this.savingTask = (new BukkitRunnable() {
            public void run() {
               final List var1 = Utils.getOnlinePlayers();
               (new BukkitRunnable() {
                  int currentPlayer = 0;

                  public void run() {
                     if (this.currentPlayer >= var1.size()) {
                        this.cancel();
                        Bukkit.getConsoleSender().sendMessage(Kitbattle.this.msgs.prefix + "Players stats have been saved!");
                     } else {
                        Player var1x = (Player)var1.get(this.currentPlayer);
                        if (var1x != null && var1x.isOnline()) {
                           PlayerData var2 = PlayerDataManager.get(var1x);
                           var2.saveStatsIntoFile(var1x, true);
                        }

                        ++this.currentPlayer;
                     }
                  }
               }).runTaskTimerAsynchronously(Kitbattle.instance, 0L, 1L);
            }
         }).runTaskTimer(this, (long)(this.getConfig().getInt("Saving-Task.Save-Every-Minutes") * 1200), (long)(this.getConfig().getInt("Saving-Task.Save-Every-Minutes") * 1200));
      } else {
         this.savingTask = null;
      }

      this.checkForUpdates();
      this.customMapsManager.clear();

      for(String var13 : this.getConfig().getConfigurationSection("Custom-Maps").getKeys(false)) {
         String var14 = "Custom-Maps." + var13 + ".";
         if (this.getConfig().getBoolean(var14 + "enabled")) {
            List var6 = this.getConfig().getStringList(var14 + "texts");
            String var7 = this.getConfig().getString(var14 + "image");
            this.customMapsManager.registerMap(var13, new CombinedMapRenderer(this, var6, var7, true));
         }
      }

   }

   public void loadMaps() {
      this.playingMaps.clear();
      this.tournamentMaps.clear();
      this.challengeMaps.clear();
      final FileConfiguration var1 = this.fileManager.getConfig("maps.yml");
      if (var1.getConfigurationSection("Maps") != null) {
         (new BukkitRunnable() {
            public void run() {
               for(String var2 : var1.getConfigurationSection("Maps").getKeys(false)) {
                  String var3 = var1.getString("Maps." + var2 + ".Type").toLowerCase();
                  boolean var4 = var1.getBoolean("Maps." + var2 + ".Enabled");
                  ArrayList var5 = new ArrayList();
                  if (var1.getConfigurationSection("Maps." + var2 + ".Spawnpoints") != null && !var1.getConfigurationSection("Maps." + var2 + ".Spawnpoints").getKeys(false).isEmpty()) {
                     for(String var7 : var1.getConfigurationSection("Maps." + var2 + ".Spawnpoints").getKeys(false)) {
                        String var8 = var1.getString("Maps." + var2 + ".Spawnpoints." + var7);
                        String[] var9 = var8.split(", ");
                        World var10 = Bukkit.getWorld(var9[0]);
                        if (var10 == null) {
                           Bukkit.getConsoleSender().sendMessage(Kitbattle.this.msgs.prefix + "The world for map " + var2 + " is not loaded! Attempting to load it...");
                           Bukkit.createWorld(new WorldCreator(var9[0]));
                        }

                        var5.add(Utils.getLocationFromString(var8));
                     }
                  }

                  if (var3.equals("tournament")) {
                     Kitbattle.this.tournamentMaps.put(var2.toLowerCase(), new TournamentMap(Kitbattle.instance, var2, var5, var4));
                  } else if (var3.equals("challenge")) {
                     Kitbattle.this.challengeMaps.put(var2.toLowerCase(), new ChallengeMap(Kitbattle.instance, var2, var5, var1.getInt("Maps." + var2 + ".Players-Per-Team"), true));
                  } else {
                     ArrayList var18 = new ArrayList();
                     if (var1.getConfigurationSection("Maps." + var2 + ".Spawn-Cuboids") != null && !var1.getConfigurationSection("Maps." + var2 + ".Spawn-Cuboids").getKeys(false).isEmpty()) {
                        for(String var23 : var1.getConfigurationSection("Maps." + var2 + ".Spawn-Cuboids").getKeys(false)) {
                           var18.add(new Cuboid(var1.getString("Maps." + var2 + ".Spawn-Cuboids." + var23)));
                        }
                     }

                     HashMap var21 = new HashMap();
                     if (var1.getConfigurationSection("Maps." + var2 + ".Signs") != null && !var1.getConfigurationSection("Maps." + var2 + ".Signs").getKeys(false).isEmpty()) {
                        for(String var26 : var1.getConfigurationSection("Maps." + var2 + ".Signs").getKeys(false)) {
                           var21.put(Utils.getLocationFromString(var1.getString("Maps." + var2 + ".Signs." + var26)), Integer.valueOf(var26));
                        }
                     }

                     Kitbattle.this.playingMaps.put(var2.toLowerCase(), new PlayingMap(Kitbattle.instance, var2, var5, var18, var4, var21));
                  }
               }

               if (Kitbattle.this.tournamentsManager != null && Kitbattle.this.tournamentMaps.isEmpty()) {
                  Kitbattle.this.tournamentsManager.stop();
               } else if (Kitbattle.this.tournamentsManager == null && !Kitbattle.this.tournamentMaps.isEmpty()) {
                  Kitbattle.this.tournamentsManager = new TournamentManager(Kitbattle.instance);
               }

               if (Kitbattle.this.challengesManager != null && Kitbattle.this.challengeMaps.isEmpty()) {
                  Kitbattle.this.challengesManager = null;
               } else if (Kitbattle.this.challengesManager == null && !Kitbattle.this.challengeMaps.isEmpty()) {
                  Kitbattle.this.challengesManager = new ChallengesManager(Kitbattle.instance);
               }

               for(Player var13 : Utils.getPlayers(Kitbattle.this.players)) {
                  PlayerData var15 = PlayerDataManager.get(var13);
                  if (var15.getMap() != null && (!Kitbattle.this.playingMaps.containsKey(var15.getMap().name.toLowerCase()) || !var15.getMap().isAvailable())) {
                     var15.getMap().removePlayers();
                  }

                  for(int var16 = 0; var16 < var15.kitsInventory.getSize(); ++var16) {
                     for(int var25 : SmartInventory.smartSlots) {
                        ItemStack var27 = var15.kitsInventory.getItem(var16, var25);
                        if (var27 != null && !Kitbattle.this.Kits.containsKey(ChatColor.stripColor(var27.getItemMeta().getDisplayName().toLowerCase()))) {
                           var15.kitsInventory.removeItem(var16, var25);
                        }
                     }
                  }
               }

               if (Kitbattle.this.config.bungeeMode) {
                  if (Kitbattle.this.bungeeMode == null) {
                     Kitbattle.this.bungeeMode = new BungeeMode(Kitbattle.instance);
                     if (Kitbattle.this.bungeeMode.getMap() != null) {
                        for(Player var14 : Utils.getOnlinePlayers()) {
                           Kitbattle.this.join(var14, Kitbattle.this.bungeeMode.getMap(), 10);
                        }
                     } else {
                        Kitbattle.this.bungeeMode.kickAll();
                     }
                  } else if (Kitbattle.this.playingMaps.containsKey(Kitbattle.this.bungeeMode.getMap().name.toLowerCase()) && ((PlayingMap)Kitbattle.this.playingMaps.get(Kitbattle.this.bungeeMode.getMap().name.toLowerCase())).isAvailable()) {
                     Kitbattle.this.bungeeMode.updateMap();
                  } else {
                     Kitbattle.this.bungeeMode.changeMap();
                  }
               }

            }
         }).runTaskLater(instance, (long)this.config.mapLoadDelay);
      }
   }

   public void clearData(Player var1) {
      var1.getInventory().clear();
      var1.getInventory().setArmorContents((ItemStack[])null);
      var1.setHealth(var1.getMaxHealth());
      var1.setFoodLevel(20);
      var1.setLevel(0);
      var1.setExp(0.0F);

      for(PotionEffect var3 : var1.getActivePotionEffects()) {
         var1.removePotionEffect(var3.getType());
      }

      var1.setGameMode(GameMode.SURVIVAL);
      var1.setAllowFlight(false);
      var1.setFlying(false);
      var1.setFireTicks(0);
   }

   public void giveDefaultItems(Player var1) {
      PlayerUtils.applyHotbarItem(var1, "Kit-Selector");
      PlayerData var2 = PlayerDataManager.get(var1);
      if ((this.tournamentsManager == null || !this.tournamentsManager.isRunning() || this.tournamentsManager.isRunning() && !this.tournamentsManager.contains(var1)) && (this.challengesManager == null || !this.challengesManager.players.contains(var1.getUniqueId()))) {
         PlayerUtils.applyHotbarItem(var1, "Shop-Opener");
         if (this.tournamentsManager != null) {
            var1.getInventory().setItem(3, (new ItemStackBuilder(!this.tournamentsManager.contains(var1) && !this.tournamentsManager.isQueueing(var1) ? XMaterial.GRAY_DYE.parseItem() : XMaterial.LIME_DYE.parseItem())).setName(ChatColor.AQUA + "Tournament: " + (!this.tournamentsManager.contains(var1) && !this.tournamentsManager.isQueueing(var1) ? ChatColor.RED + "Disabled" : ChatColor.GREEN + "Enabled")).build());
         }

         if (this.challengesManager != null) {
            PlayerUtils.applyHotbarItem(var1, "Challenges-Item");
         }

         if (this.trailsInventory != null && var1.hasPermission("kitbattle.trails")) {
            PlayerUtils.applyHotbarItem(var1, "Trails-Opener");
         }

         PlayerUtils.applyHotbarItem(var1, "Profile-Item");
         if (var2.kitUnlockers > 0) {
            PlayerUtils.applyHotbarItem(var1, "Kit-Unlocker", var2.kitUnlockers);
         }
      }

      var2.clearCooldowns();
      var1.updateInventory();
   }

   public boolean isMaterial(String var1) {
      try {
         Material.valueOf(var1);
         return true;
      } catch (IllegalArgumentException var3) {
         return false;
      }
   }

   public void join(final Player var1, PlayingMap var2, int var3) {
      final PlayerData var4 = PlayerDataManager.get(var1);
      this.players.add(var1.getUniqueId());
      if (this.config.AllowBuilding && !this.editmode.contains(var1.getUniqueId())) {
         this.editmode.add(var1.getUniqueId());
      }

      var4.saveData(var1, var2);
      var1.teleport(var2.getSpawnpoint());
      this.clearData(var1);
      (new BukkitRunnable() {
         public void run() {
            Kitbattle.this.giveDefaultItems(var1);
            var4.createScoreboard(var1);
         }
      }).runTaskLater(instance, (long)var3);
      if (this.config.scoreboardTitleAnimationEnabled && this.scoreboardTitleAnimationTask == null) {
         this.startTitleAnimation();
      }

   }

   public void resetPlayerToMap(Player var1, Map var2, boolean var3) {
      this.spectating.remove(var1.getUniqueId());
      PlayerData var4 = PlayerDataManager.get(var1);
      if (var3) {
         var4.setMap(var1, (PlayingMap)var2);
      }

      if (!var1.isDead()) {
         this.clearData(var1);
         var1.teleport(var2.getSpawnpoint());
         this.giveDefaultItems(var1);
      }

      var4.setKit(var1, (Kit)null);
      var4.killstreak = 0;
      var4.deathstreak = 0;
      var4.damagers.clear();
      if (var4.customScoreboard != null) {
         var4.customScoreboard.updatePlaceholder("%killstreak%", 0);
         var4.customScoreboard.updatePlaceholder("%deathstreak%", 0);
      }

   }

   private void startTitleAnimation() {
      this.scoreboardTitleAnimationTask = (new BukkitRunnable() {
         int index = 0;

         public void run() {
            String var1 = (String)Kitbattle.this.config.scoreboardTitleAnimationFrames.get(this.index);
            if (++this.index >= Kitbattle.instance.config.scoreboardTitleAnimationFrames.size()) {
               this.index = 0;
            }

            for(UUID var4 : (List)Kitbattle.this.players.clone()) {
               PlayerData var5 = PlayerDataManager.get(Bukkit.getPlayer(var4));
               if (var5.customScoreboard != null) {
                  var5.customScoreboard.setName(var1);
               }
            }

         }
      }).runTaskTimer(this, (long)this.config.scoreboardTitleAnimationInterval, (long)this.config.scoreboardTitleAnimationInterval);
   }

   public void leave(Player var1) {
      if (this.tournamentsManager != null) {
         this.tournamentsManager.remove(var1, true);
      }

      if (this.challengesManager != null) {
         if (this.challengesManager.players.contains(var1.getUniqueId())) {
            for(ChallengeMap var3 : this.challengeMaps.values()) {
               if (var3.players.containsKey(var1.getUniqueId())) {
                  var3.remove(var1, true);
                  break;
               }
            }
         } else {
            this.challengesManager.removeFromQueues(var1);
         }
      }

      this.players.remove(var1.getUniqueId());
      this.spectating.remove(var1.getUniqueId());
      PlayerData var4 = PlayerDataManager.get(var1);
      var4.killstreak = 0;
      var4.deathstreak = 0;
      var4.restoreData(var1);
      var1.sendMessage((String)this.msgs.messages.get("Player-Leave"));
      if (this.scoreboardTitleAnimationTask != null && this.players.isEmpty()) {
         this.scoreboardTitleAnimationTask.cancel();
         this.scoreboardTitleAnimationTask = null;
      }

      if (this.bungeeMode != null && this.bungeeMode.playerVotes != null && this.bungeeMode.playerVotes.containsKey(var1.getUniqueId())) {
         String var5 = (String)this.bungeeMode.playerVotes.get(var1.getUniqueId());
         this.bungeeMode.playerVotes.remove(var1.getUniqueId());
         this.bungeeMode.updateVotes(var5);
      }

   }

   private void setupEcon() {
      RegisteredServiceProvider var1 = this.getServer().getServicesManager().getRegistration(Economy.class);
      if (var1 != null) {
         this.econ = (Economy)var1.getProvider();
      }

   }

   public void cageInventory(Inventory var1, boolean var2) {
      if (var2) {
         for(int var7 = 0; var7 < var1.getSize(); ++var7) {
            var1.setItem(var7, this.pane_itemstack);
         }

      } else {
         for(int var3 = 0; var3 < 9; ++var3) {
            var1.setItem(var3, this.pane_itemstack);
         }

         for(int var5 = var1.getSize() - 9; var5 < var1.getSize(); ++var5) {
            var1.setItem(var5, this.pane_itemstack);
         }

         int var6 = var1.getSize() / 9 - 2;
         if (var6 >= 1) {
            for(int var4 = 9; var4 < 9 * var6 + 1; var4 += 9) {
               var1.setItem(var4, this.pane_itemstack);
            }

            for(int var8 = 17; var8 < 9 * (var6 + 1); var8 += 9) {
               var1.setItem(var8, this.pane_itemstack);
            }

         }
      }
   }

   public boolean isInTournament(Player var1) {
      return this.tournamentsManager != null && this.tournamentsManager.isRunning() && this.tournamentsManager.contains(var1);
   }

   public boolean isInChallenge(Player var1) {
      return this.challengesManager != null && this.challengesManager.players.contains(var1.getUniqueId());
   }

   private void updateHead(Sign var1, String var2) {
      if (var2.contains("NO_PLAYER")) {
         var2 = "MHF_Question";
      }

      Location[] var3 = new Location[]{var1.getLocation().add((double)0.0F, (double)1.0F, (double)0.0F), var1.getLocation().clone().add((double)1.0F, (double)1.0F, (double)0.0F), var1.getLocation().clone().add((double)-1.0F, (double)1.0F, (double)0.0F), var1.getLocation().clone().add((double)0.0F, (double)1.0F, (double)1.0F), var1.getLocation().clone().add((double)0.0F, (double)1.0F, (double)-1.0F)};

      for(Location var7 : var3) {
         if (var7.getBlock().getState() instanceof Skull) {
            Skull var8 = (Skull)var7.getBlock().getState();
            var8.setOwner(var2);
            var8.update();
            break;
         }
      }

   }

   public void startLeaderboardUpdater() {
      if (this.leaderboard_updater != null) {
         this.leaderboard_updater.cancel();
      }

      this.leaderboard_updater = (new BukkitRunnable() {
         public void run() {
            Bukkit.getConsoleSender().sendMessage(Kitbattle.this.msgs.prefix + "Updating leaderboards...");
            final HashMap var1 = new HashMap();
            final HashMap var2 = new HashMap();
            final HashMap var3 = new HashMap();

            for(Location var5 : Kitbattle.this.topSigns.keySet()) {
               Block var6 = var5.getBlock();
               if (var6.getState() instanceof Sign) {
                  Sign var7 = (Sign)var6.getState();
                  if (var7.getLine(0).startsWith(ChatColor.AQUA + "Top #" + ChatColor.RED) && KitbattleAPI.Stat.getByName(var7.getLine(1).toUpperCase()) != null && Utils.checkNumbers(ChatColor.stripColor(var7.getLine(0).split("#")[1]))) {
                     int var8 = Integer.parseInt(ChatColor.stripColor(var7.getLine(0).split("#")[1]));
                     KitbattleAPI.Stat var9 = KitbattleAPI.Stat.valueOf(var7.getLine(1).toUpperCase());
                     var2.put(var5, var8);
                     var3.put(var5, var9);
                     if (!var1.containsKey(var9) || (Integer)var1.get(var9) < var8) {
                        var1.put(var9, var8);
                     }
                  } else {
                     Bukkit.getConsoleSender().sendMessage(Kitbattle.this.msgs.prefix + "Your top sign with the id of " + ChatColor.AQUA + "#" + (Integer)Kitbattle.this.topSigns.get(var5) + ChatColor.GOLD + " isn't really a top sign");
                  }
               } else {
                  Bukkit.getConsoleSender().sendMessage(Kitbattle.this.msgs.prefix + "Your top sign with the id of " + ChatColor.AQUA + "#" + (Integer)Kitbattle.this.topSigns.get(var5) + ChatColor.GOLD + " doesn't exist at the coordinates " + ChatColor.GREEN + Utils.getReadableLocationString(var5, false));
               }
            }

            if (var2.isEmpty()) {
               Bukkit.getConsoleSender().sendMessage(Kitbattle.this.msgs.prefix + "Couldn't find any valid top sign to update!");
               if (Kitbattle.this.hologramsManager == null || Kitbattle.this.hologramsManager.leaderboardHologram == null) {
                  Bukkit.getConsoleSender().sendMessage(Kitbattle.this.msgs.prefix + "No Holographic leaderboard was set either, cancelling the task!");
                  this.cancel();
                  Kitbattle.this.leaderboard_updater = null;
                  return;
               }
            }

            (new BukkitRunnable() {
               public void run() {
                  try {
                     HashMap var1x = KitbattleAPI.getAllPlayersData();
                     final HashMap var2x = new HashMap();
                     final HashMap var3x = new HashMap();

                     for(KitbattleAPI.Stat var5 : var1.keySet()) {
                        int var6 = (Integer)var1.get(var5);
                        List var7 = KitbattleAPI.getTopPlayers(var1x, var5, var6);
                        var3x.put(var5, var7);
                        var2x.put(var5, (java.util.Map.Entry)var7.get(0));
                     }

                     if (Kitbattle.this.hologramsManager != null && Kitbattle.this.hologramsManager.leaderboardHologram != null) {
                        for(KitbattleAPI.Stat var12 : KitbattleAPI.Stat.values()) {
                           if (!var2x.containsKey(var12)) {
                              var2x.put(var12, (java.util.Map.Entry)KitbattleAPI.getTopPlayers(var1x, var12, 1).get(0));
                           }
                        }
                     }

                     (new BukkitRunnable() {
                        public void run() {
                           for(KitbattleAPI.Stat var2xx : var1.keySet()) {
                              List var3xx = (List)var3x.get(var2xx);

                              for(Location var5 : var3.keySet()) {
                                 if (((KitbattleAPI.Stat)var3.get(var5)).equals(var2xx)) {
                                    Sign var6 = (Sign)var5.getBlock().getState();
                                    java.util.Map.Entry var7 = (java.util.Map.Entry)var3xx.get((Integer)var2.get(var5) - 1);
                                    var6.setLine(2, (String)var7.getKey());
                                    var6.setLine(3, "(" + var7.getValue() + ")");
                                    var6.update();
                                    Kitbattle.this.updateHead(var6, ((String)var7.getKey()).contains("NO_PLAYER") ? "MHF_Question" : (String)var7.getKey());
                                 }
                              }
                           }

                           if (Kitbattle.this.hologramsManager != null && Kitbattle.this.hologramsManager.leaderboardHologram != null) {
                              Kitbattle.this.hologramsManager.updateLeaderboards(var2x);
                           }

                           Kitbattle.this.leaderboard_updater_time = System.currentTimeMillis() + (long)(Kitbattle.this.getConfig().getInt("Update-Leaderboard-Every-Minutes") * '\uea60');
                           Bukkit.getConsoleSender().sendMessage(Kitbattle.this.msgs.prefix + "Leaderboards were updated!");
                        }
                     }).runTask(Kitbattle.instance);
                  } catch (SQLException var8) {
                     var8.printStackTrace();
                  }

               }
            }).runTaskAsynchronously(Kitbattle.instance);
         }
      }).runTaskTimer(this, 0L, (long)(this.getConfig().getInt("Update-Leaderboard-Every-Minutes") * 1200));
   }

   public double getModifier(Player var1) {
      double var2 = (double)1.0F;

      for(String var5 : instance.config.modifiers.keySet()) {
         if (var1.hasPermission(var5) && (Double)instance.config.modifiers.get(var5) > var2) {
            var2 = (Double)instance.config.modifiers.get(var5);
         }
      }

      return var2;
   }

   protected void createDefaultAchievements() {
      FileConfiguration var1 = this.fileManager.getConfig("achievements.yml");

      for(AchievementsManager.AchievementType var5 : AchievementsManager.AchievementType.values()) {
         if (!var1.contains("Achievements." + var5.name().toLowerCase())) {
            for(int var9 : var5.levels) {
               String var10 = "Achievements." + var5.name().toLowerCase() + "." + var9 + ".";
               var1.set(var10 + "description", var5.defaultDescription.replace("%x%", String.valueOf(var9)));
               var1.set(var10 + "prize-description", "Earn " + var5.prizeMultiplier * var9 + " coins!");
               var1.set(var10 + "executed-command", "kb coins add %player% " + var5.prizeMultiplier * var9);
            }
         }
      }

      this.fileManager.saveConfig("achievements.yml");
   }

   public void checkForUpdates() {
      this.availableUpdate = false;
      if (this.getConfig().getBoolean("Check-For-Updates")) {
         (new BukkitRunnable() {
            public void run() {
               try {
                  HttpURLConnection var1 = (HttpURLConnection)(new URL("https://api.spigotmc.org/legacy/update.php?resource=2872")).openConnection();
                  var1.setRequestMethod("GET");
                  String var2 = (new BufferedReader(new InputStreamReader(var1.getInputStream()))).readLine();
                  Kitbattle.this.availableUpdate = !var2.equals(Kitbattle.this.getDescription().getVersion());
                  String var3 = Kitbattle.this.msgs.prefix + (Kitbattle.this.availableUpdate ? "Found a new available version! " + ChatColor.LIGHT_PURPLE + "download at https://goo.gl/acFc6M" : "Looks like you have the latest version installed!");
                  Bukkit.getConsoleSender().sendMessage(var3);

                  for(Player var5 : Utils.getOnlinePlayers()) {
                     if (var5.hasPermission("skywars.admin")) {
                        var5.sendMessage(var3);
                     }
                  }
               } catch (IOException var6) {
                  Bukkit.getConsoleSender().sendMessage(Kitbattle.this.msgs.prefix + "Couldn't check for an available update");
                  var6.printStackTrace();
               }

            }
         }).runTaskAsynchronously(this);
      }

   }

   public String getProgressBar(int var1) {
      if (var1 >= 20) {
         return ChatColor.GREEN + this.progressBar;
      } else {
         return var1 < 1 ? ChatColor.GRAY + this.progressBar : ChatColor.GREEN + this.progressBar.substring(0, var1) + ChatColor.GRAY + this.progressBar.substring(var1);
      }
   }

   public void sendUseAbility(Player var1, PlayerData var2) {
      var1.playSound(var1.getLocation(), SoundsManager.CLICK, 1.0F, 1.0F);
      var2.addAbilitiesUsed(var1);
   }

   private void setupMetrics() {
      short var1 = 10736;
      Metrics var2 = new Metrics(this, var1);
      var2.addCustomChart(new Metrics.SimplePie("using_bungee-mode", () -> String.valueOf(this.config.bungeeMode)));
      var2.addCustomChart(new Metrics.SimplePie("using_mysql", () -> String.valueOf(this.config.useMySQL)));
   }

   private void convertPlayerFiles() {
      File var1 = new File(this.getDataFolder(), "players.yml");
      if (var1.exists()) {
         YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var1);
         if (((FileConfiguration)var2).getConfigurationSection("Players") != null) {
            for(String var4 : ((FileConfiguration)var2).getConfigurationSection("Players").getKeys(false)) {
               File var5 = new File(this.getDataFolder() + "/players/", var4);
               YamlConfiguration var6 = YamlConfiguration.loadConfiguration(var5);
               ((FileConfiguration)var6).set("Name", ((FileConfiguration)var2).getString("Players." + var4 + ".Name"));
               ((FileConfiguration)var6).set("Statistics", ((FileConfiguration)var2).getString("Players." + var4 + ".Statistics"));
               ((FileConfiguration)var6).set("Kits", ((FileConfiguration)var2).getStringList("Players." + var4 + ".Kits"));

               try {
                  ((FileConfiguration)var6).save(var5);
               } catch (IOException var8) {
                  Bukkit.getConsoleSender().sendMessage("[KitBattle] Failed to move the stats of player: " + var4 + " into the new players folder!");
               }
            }
         }

         var1.delete();
      }

   }
}
