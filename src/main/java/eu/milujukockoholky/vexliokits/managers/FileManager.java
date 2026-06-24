package eu.milujukockoholky.vexliokits.managers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import eu.milujukockoholky.vexliokits.Kit;
import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.VexlioKitsAPI;
import eu.milujukockoholky.vexliokits.Rank;
import eu.milujukockoholky.vexliokits.utils.ItemStackBuilder;
import eu.milujukockoholky.vexliokits.utils.Utils;
import eu.milujukockoholky.vexliokits.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

public class FileManager {
   List<String> armorParts = Arrays.asList("Boots", "Leggings", "Chestplate", "Helmet");
   private final HashMap<String, FileConfiguration> configurations = new HashMap();
   private final VexlioKits plugin;

   public FileManager(VexlioKits var1) {
      this.plugin = var1;
      var1.reloadConfig();
      var1.getConfig().options().copyDefaults(true);
      var1.saveConfig();
      this.registerConfig("maps.yml");
      this.registerConfig("kits.yml");
      this.registerConfig("ranks.yml");
      this.registerConfig("messages.yml");
      this.registerConfig("abilities.yml");
      this.registerConfig("signs.yml");
      this.registerConfig("shop.yml");
      this.registerConfig("achievements.yml");
      this.registerConfig("trails_blacklist.yml");

      for(String var3 : this.configurations.keySet()) {
         this.reloadConfig(var3);
         ((FileConfiguration)this.configurations.get(var3)).options().copyDefaults(true);
         this.saveConfig(var3);
      }

   }

   private void registerConfig(String var1) {
      this.configurations.put(var1, YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), var1)));
   }

   public FileConfiguration getConfig(String var1) {
      return (FileConfiguration)this.configurations.get(var1);
   }

   private void reloadConfig(String var1) {
      InputStream var2 = this.plugin.getResource(var1);
      if (var2 != null) {
         InputStreamReader var3 = new InputStreamReader(var2);
         YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
         ((FileConfiguration)this.configurations.get(var1)).setDefaults(var4);

         try {
            var3.close();
            var2.close();
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }

   }

   public void saveConfig(String var1) {
      try {
         ((FileConfiguration)this.configurations.get(var1)).save(new File(this.plugin.getDataFolder(), var1));
      } catch (Exception var3) {
         Bukkit.getConsoleSender().sendMessage(this.plugin.msgs.prefix + "Couldn't save " + var1 + "!");
      }

   }

   public void setupKits() {
      FileConfiguration var1 = this.getConfig("kits.yml");
      if (this.plugin.getConfig().getBoolean("Create-Default-Kits")) {
         this.plugin.getConfig().set("Create-Default-Kits", false);
         this.plugin.saveConfig();
         var1.addDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(this.plugin.getResource("default-kits.yml"))));
         var1.options().copyDefaults(true);
      }

      this.saveConfig("kits.yml");
   }

   public void loadKits() {
      FileConfiguration var1 = this.getConfig("kits.yml");
      if (var1.getConfigurationSection("Kits") != null) {
         boolean var2 = false;
         ArrayList<String> var3 = new ArrayList<>();

         for(String var5 : var1.getConfigurationSection("Kits").getKeys(false)) {
            ItemStack[] var7 = new ItemStack[4];
            ItemStack[] var8 = new ItemStack[36];
            boolean var9 = var1.getBoolean("Kits." + var5 + ".Enabled");
            boolean var10 = var1.getBoolean("Kits." + var5 + ".Require-Permission");

            ItemStackBuilder var6;
            try {
               String var11 = var1.getString("Kits." + var5 + ".Item");
               Optional var12 = XMaterial.matchXMaterial(var11);
               if (!var12.isPresent() && var11.contains(":")) {
                  var12 = XMaterial.matchXMaterial(var11.split(":")[0]);
               }

               var6 = new ItemStackBuilder(((XMaterial)var12.get()).parseItem());
               if (var11.contains(":")) {
                  String var13 = var11.split(":")[0];
                  if ((var13.equals("POTION") || var13.equals("SPLASH_POTION") || var13.equals("LINGERING_POTION")) && var11.split(":").length == 4) {
                     var6.setPotionEffect(PotionType.valueOf(var11.split(":")[1]), Boolean.valueOf(var11.split(":")[2]), Boolean.valueOf(var11.split(":")[3]));
                  } else {
                     var6.setDurability(Integer.valueOf(var11.split(":")[1]));
                  }
               }

               var6.setName(ChatColor.GREEN + var5);

               for(String var14 : var1.getStringList("Kits." + var5 + ".Description")) {
                  var6.addLore(Utils.colorize(var14));
               }
            } catch (Exception var20) {
               Bukkit.getConsoleSender().sendMessage("[VexlioKits] Failed to create the logo for the kit: " + var5 + ", due to that, the whole kit wont load!, make sure you have the correct format!");
               var20.printStackTrace();
               continue;
            }

            int var23 = 0;

            for(String var27 : this.armorParts) {
               try {
                  var7[var23] = var1.getString("Kits." + var5 + ".Armor." + var27).isEmpty() ? new ItemStack(Material.AIR) : Utils.getItemStack(var1.getString("Kits." + var5 + ".Armor." + var27), true, true);
                  ++var23;
               } catch (Exception var18) {
                  Bukkit.getConsoleSender().sendMessage("[VexlioKits] Failed to create a " + var27 + " for the kit: " + var5 + ", due to that, the whole kit wont load!, make sure you have the correct format!");
                  var18.printStackTrace();
               }
            }

            int var25 = 0;

            for(String var30 : var1.getStringList("Kits." + var5 + ".Items")) {
               try {
                  if (var30.equalsIgnoreCase("AUTO_FILL_HEAL_ITEM")) {
                     for(int var15 = 0; var15 < var8.length; ++var15) {
                        if (var8[var15] == null) {
                           var8[var15] = this.plugin.listen.soup;
                        }
                     }
                     break;
                  }

                  if (var25 == var8.length) {
                     break;
                  }

                  var8[var25++] = var30.isEmpty() ? new ItemStack(Material.AIR) : Utils.getItemStack(var30, true, true);
               } catch (Exception var19) {
                  Bukkit.getConsoleSender().sendMessage("[VexlioKits] Failed to create this item: " + var30 + " for the kit: " + var5 + ", due to that, the whole kit wont load!, make sure you have the correct format!");
                  var19.printStackTrace();
               }
            }

            int var29 = var1.getInt("Kits." + var5 + ".Price");
            ArrayList var31 = new ArrayList();

            for(String var16 : var1.getStringList("Kits." + var5 + ".Potion-Effects")) {
               String[] var17 = var16.split(" : ");
               var31.add(new PotionEffect(PotionEffectType.getByName(var17[0]), Integer.valueOf(var17[1]) * 20, Integer.valueOf(var17[2]) - 1));
            }

            ArrayList var33 = new ArrayList();

            for(String var36 : var1.getStringList("Kits." + var5 + ".Abilities")) {
               var33.add(var36.toLowerCase());
            }

            List var35 = var1.getStringList("Kits." + var5 + ".Executed-Commands");
            if (!var1.contains("Kits." + var5 + ".Selection-Cooldown")) {
               var1.set("Kits." + var5 + ".Selection-Cooldown", 0);
               var2 = true;
            }

            int var37 = var1.getInt("Kits." + var5 + ".Selection-Cooldown");
            if (var1.contains("Kits." + var5 + ".Upgraded-Version-Of")) {
               var3.add(var5);
            }

            this.plugin.Kits.put(var5.toLowerCase(), new Kit(this.plugin, var5, var6.build(), var8, var7, var29, var37, var31, var33, var9, var10, var35));
         }

         if (var2) {
            this.saveConfig("kits.yml");
         }

         for(String var22 : var3) {
            if (this.plugin.Kits.containsKey(var1.getString("Kits." + var22 + ".Upgraded-Version-Of").toLowerCase())) {
               ((Kit)this.plugin.Kits.get(var22.toLowerCase())).original = (Kit)this.plugin.Kits.get(var1.getString("Kits." + var22 + ".Upgraded-Version-Of").toLowerCase());
            }
         }

      }
   }

   public void setupRanks() {
      FileConfiguration var1 = this.getConfig("ranks.yml");
      if (var1.getConfigurationSection("Ranks") == null) {
         var1.set("Ranks.Newbie.Required-Exp", 0);
         var1.set("Ranks.Newbie.Commands-Excuted-When-Rank-Reached", Arrays.asList());
         var1.set("Ranks.Starter.Required-Exp", 25);
         var1.set("Ranks.Starter.Commands-Excuted-When-Rank-Reached", Arrays.asList("vexliokits coins add %player% 500", "vexliokits kitunlocker give %player% 1"));
         var1.set("Ranks.Survivor.Required-Exp", 75);
         var1.set("Ranks.Survivor.Commands-Excuted-When-Rank-Reached", Arrays.asList("vexliokits coins add %player% 750", "vexliokits kitunlocker give %player% 1"));
         var1.set("Ranks.Pro.Required-Exp", 150);
         var1.set("Ranks.Pro.Commands-Excuted-When-Rank-Reached", Arrays.asList("vexliokits coins add %player% 1000", "vexliokits kitunlocker give %player% 1"));
         var1.set("Ranks.Legend.Required-Exp", 300);
         var1.set("Ranks.Legend.Commands-Excuted-When-Rank-Reached", Arrays.asList("vexliokits coins add %player% 2000", "vexliokits kitunlocker give %player% 1"));
         var1.set("Ranks.Immortal.Required-Exp", 600);
         var1.set("Ranks.Immortal.Commands-Excuted-When-Rank-Reached", Arrays.asList("vexliokits coins add %player% 4000", "vexliokits kitunlocker give %player% 1"));
         var1.set("Ranks.God.Prefix", "&5[&ka&4God&5&ka&5] &c");
         var1.set("Ranks.God.Required-Exp", 1200);
         var1.set("Ranks.God.Commands-Excuted-When-Rank-Reached", Arrays.asList("vexliokits coins add %player% 5000", "vexliokits kitunlocker give %player% 1"));
      }

      this.saveConfig("ranks.yml");
   }

   public void loadRanks() {
      FileConfiguration var1 = this.getConfig("ranks.yml");
      if (var1.getConfigurationSection("Ranks") != null) {
         for(String var3 : var1.getConfigurationSection("Ranks").getKeys(false)) {
            int var4 = var1.getInt("Ranks." + var3 + ".Required-Exp");
            List var5 = var1.getStringList("Ranks." + var3 + ".Commands-Excuted-When-Rank-Reached");
            String var6 = Utils.colorize(var1.contains("Ranks." + var3 + ".Prefix") ? var1.getString("Ranks." + var3 + ".Prefix") : var1.getString("General-Ranks-Prefix")).replace("%rank%", var3);
            this.plugin.Ranks.put(var3.toLowerCase(), new Rank(var3, var6, var4, var5));
         }

      }
   }

   public void executeDatabaseUpdate(final CommandSender var1, final String var2, final BukkitRunnable var3, final String[] var4) {
      var1.sendMessage(this.plugin.msgs.prefix + ChatColor.YELLOW + "Couldn't find that player online, looking up the database...");
      if (this.plugin.config.useMySQL) {
         (new BukkitRunnable() {
            public void run() {
               try {
                  if (!FileManager.this.plugin.mysql.getConnection().createStatement().executeQuery("select * from " + FileManager.this.plugin.config.tableprefix + " WHERE player_name= '" + var2 + "'").next()) {
                     var1.sendMessage(FileManager.this.plugin.msgs.prefix + "Couldn't find that player!");
                     return;
                  }

                  var4[0] = var2;
                  var3.runTaskAsynchronously(FileManager.this.plugin);
               } catch (SQLException var2x) {
                  var2x.printStackTrace();
               }

            }
         }).runTaskAsynchronously(this.plugin);
      } else {
         List<File> var5 = VexlioKitsAPI.getPlayersFiles();
         if (this.plugin.config.UUID) {
            for(File var10 : var5) {
               YamlConfiguration var11 = YamlConfiguration.loadConfiguration(var10);
               if (((FileConfiguration)var11).getString("Name").equalsIgnoreCase(var2)) {
                  var4[0] = var10.getName();
                  var4[1] = ((FileConfiguration)var11).getString("Name");
                  var3.run();
                  return;
               }
            }
         } else {
            for(File var7 : var5) {
               if (var7.getName().equalsIgnoreCase(var2)) {
                  YamlConfiguration var8 = YamlConfiguration.loadConfiguration(var7);
                  var4[0] = var7.getName();
                  var4[1] = ((FileConfiguration)var8).getString("Name");
                  var3.run();
                  return;
               }
            }
         }

         var1.sendMessage(this.plugin.msgs.prefix + "Couldn't find that player!");
      }

   }
}
