package eu.milujukockoholky.vexliokits.commands.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import eu.milujukockoholky.vexliokits.Kit;
import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.VexlioKitsAPI;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
import eu.milujukockoholky.vexliokits.utils.ItemStackBuilder;
import eu.milujukockoholky.vexliokits.utils.SmartInventory;
import eu.milujukockoholky.vexliokits.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class KitCommand extends SubCommand {
   public KitCommand() {
      super("VexlioKits.kit", true, "Give/Create/Delete/Rename/Enable/Disable");
   }

   public boolean execute(VexlioKits var1, final Messages var2, final CommandSender var3, final String[] var4) {
      if (var4.length != 1 && (var4[1].equalsIgnoreCase("Give") || var4[1].equalsIgnoreCase("Create") || var4[1].equalsIgnoreCase("Delete") || var4[1].equalsIgnoreCase("Rename") || var4[1].equalsIgnoreCase("Enable") || var4[1].equalsIgnoreCase("Disable"))) {
         String var5 = var4[1].toLowerCase();
         if (var5.equals("give")) {
            if (var4.length < 4) {
               var3.sendMessage(var2.prefix + "Usage: /vexliokits " + ChatColor.GREEN + "Kit " + ChatColor.GRAY + "Give <Player> <Kit>");
               return true;
            }

            Player var6 = Bukkit.getPlayer(var4[2]);
            if (var6 == null) {
               var3.sendMessage((String)var2.messages.get("Player-Not-Found"));
               return true;
            }

            Kit var7 = (Kit)var1.Kits.get(var4[3].toLowerCase());
            if (var7 == null) {
               var3.sendMessage(var2.prefix + "Couldn't find a kit with that name!");
               return true;
            }

            PlayerData var8 = PlayerDataManager.get(var6);
            Map.Entry var9 = var8.kitsInventory.getEmptySlot();
            var8.kitsInventory.setItem((Integer)var9.getKey(), (Integer)var9.getValue(), var7.getLogo());
            var3.sendMessage(var2.prefix + "The kit " + ChatColor.AQUA + var7.getName() + ChatColor.GRAY + " has been given to the player " + ChatColor.LIGHT_PURPLE + var6.getName());
         } else if (var5.equals("create")) {
            if (var3 instanceof Player) {
               Player var33 = (Player)var3;
               if (var4.length < 6 || !var1.isMaterial(var4[3].split(":")[0].toUpperCase()) || !Utils.checkNumbers(var4[4], var4[5])) {
                  var33.sendMessage(var2.prefix + "Usage: /vexliokits " + ChatColor.GREEN + "Kit " + ChatColor.GRAY + "Create <Name> <Logo " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE + "(MATERIAL ENUM) " + ChatColor.GRAY + "> <Price> <Selection Cooldown>");
                  return true;
               }

               String var38 = var4[2];
               if (var1.Kits.containsKey(var38.toLowerCase())) {
                  var33.sendMessage(var2.prefix + "There is already a kit with that name!");
                  return true;
               }

               int var43 = Integer.parseInt(var4[4]);
               int var48 = Integer.parseInt(var4[5]);
               ItemStackBuilder var10 = var4[3].contains(":") ? (new ItemStackBuilder(Material.valueOf(var4[3].split(":")[0].toUpperCase()))).setDurability(Integer.valueOf(var4[3].split(":")[1])).setName(ChatColor.LIGHT_PURPLE + var38) : (new ItemStackBuilder(Material.valueOf(var4[3].toUpperCase()))).setName(ChatColor.GREEN + var38);
               var10.addLore(ChatColor.AQUA + "There is no description for this kit!");
               ItemStack[] var11 = var33.getInventory().getContents();
               ItemStack[] var12 = var33.getInventory().getArmorContents();
               Collection<PotionEffect> var13 = var33.getActivePotionEffects();
               Kit var14 = new Kit(var1, var38, var10.build(), var11, var12, var43, var48, var13, new ArrayList(), true, false, new ArrayList());
               var1.Kits.put(var38.toLowerCase(), var14);
               FileConfiguration var15 = var1.fileManager.getConfig("kits.yml");
               var15.set("Kits." + var38 + ".Enabled", true);
               var15.set("Kits." + var38 + ".Require-Permission", false);
               var15.set("Kits." + var38 + ".Item", var4[3].toUpperCase());
               var15.set("Kits." + var38 + ".Price", var43);
               var15.set("Kits." + var38 + ".Selection-Cooldown", var48);
               var15.set("Kits." + var38 + ".Armor.Helmet", Utils.itemStackToString(var33.getInventory().getHelmet()));
               var15.set("Kits." + var38 + ".Armor.Chestplate", Utils.itemStackToString(var33.getInventory().getChestplate()));
               var15.set("Kits." + var38 + ".Armor.Leggings", Utils.itemStackToString(var33.getInventory().getLeggings()));
               var15.set("Kits." + var38 + ".Armor.Boots", Utils.itemStackToString(var33.getInventory().getBoots()));
               ArrayList var16 = new ArrayList();

               for(ItemStack var20 : var11) {
                  var16.add(Utils.itemStackToString(var20));
               }

               var15.set("Kits." + var38 + ".Items", var16);
               ArrayList var79 = new ArrayList();

               for(PotionEffect var84 : var13) {
                  var79.add(var84.getType().getName() + " : " + var84.getDuration() / 20 + " : " + var84.getAmplifier());
               }

               var15.set("Kits." + var38 + ".Abilities", Arrays.asList());
               var15.set("Kits." + var38 + ".Potion-Effects", var79);
               var15.set("Kits." + var38 + ".Executed-Commands", Arrays.asList());
               var15.set("Kits." + var38 + ".Description", Arrays.asList("&bThere is no description for this kit!"));
               var1.fileManager.saveConfig("kits.yml");
               var33.getInventory().clear();
               var33.getInventory().setArmorContents((ItemStack[])null);

               for(PotionEffect var85 : var33.getActivePotionEffects()) {
                  var33.removePotionEffect(var85.getType());
               }

               var33.sendMessage(var2.prefix + "The " + ChatColor.GREEN + var38 + ChatColor.GRAY + " kit has been created!");
            } else {
               var3.sendMessage((String)var2.messages.get("No-Console"));
            }
         } else if (var5.equals("delete")) {
            if (var4.length == 2) {
               var3.sendMessage(var2.prefix + "Usage: /vexliokits " + ChatColor.GREEN + "Delete" + ChatColor.GRAY + " <Kit>");
               return true;
            }

            String var34 = var4[2].toLowerCase();
            if (!var1.Kits.containsKey(var34)) {
               var3.sendMessage(var2.prefix + "Couldn't find a kit with that name!");
               return true;
            }

            String var39 = ((Kit)var1.Kits.get(var34)).getName();
            ItemStack var44 = ((Kit)var1.Kits.get(var34)).getLogo();
            ItemStack var49 = ((Kit)var1.Kits.get(var34)).getShopLogo();
            var1.Kits.remove(var34);
            var1.fileManager.getConfig("kits.yml").set("Kits." + var39, (Object)null);
            var1.fileManager.saveConfig("kits.yml");
            var1.shop.updateItem(var49, new ItemStack(Material.AIR));

            for(PlayerData var57 : PlayerDataManager.getAll().values()) {
               for(int var61 = 0; var61 < var57.kitsInventory.getSize(); ++var61) {
                  for(int var77 : SmartInventory.smartSlots) {
                     if (var57.kitsInventory.getItem(var61, var77) != null && var57.kitsInventory.getItem(var61, var77).equals(var44)) {
                        var57.kitsInventory.removeItem(var61, var77);
                     }
                  }
               }
            }

            var3.sendMessage(var2.prefix + "The kit " + ChatColor.GREEN + var39 + ChatColor.GRAY + " has been deleted!");
         } else if (var5.equals("rename")) {
            if (var1.config.useMySQL) {
               var3.sendMessage(var2.prefix + "This command is disabled for MySql users as of now!");
               return true;
            }

            if (var4.length < 4) {
               var3.sendMessage(var2.prefix + "Usage: /vexliokits " + ChatColor.GREEN + "Kit" + ChatColor.GRAY + " rename <Kit> <New Name>");
               return true;
            }

            String var35 = var4[2].toLowerCase();
            if (!var1.Kits.containsKey(var35)) {
               var3.sendMessage(var2.prefix + "Couldn't find a kit with that name!");
               return true;
            }

            if (var1.Kits.containsKey(var4[3].toLowerCase())) {
               var3.sendMessage(var2.prefix + "There is already a kit with that name!");
               return true;
            }

            Kit var40 = (Kit)var1.Kits.get(var35);
            String var45 = "Kits." + var40.getName() + ".";
            FileConfiguration var50 = var1.fileManager.getConfig("kits.yml");
            boolean var54 = var50.getBoolean(var45 + "Enabled");
            boolean var58 = var50.getBoolean(var45 + "Require-Permission");
            String var62 = var50.getString(var45 + "Item");
            int var66 = var50.getInt(var45 + "Price");
            int var70 = var50.getInt(var45 + "Selection-Cooldown");
            String var74 = var50.getString(var45 + "Armor.Helmet");
            String var78 = var50.getString(var45 + "Armor.Chestplate");
            String var80 = var50.getString(var45 + "Armor.Leggings");
            String var83 = var50.getString(var45 + "Armor.Boots");
            List var86 = var50.getStringList(var45 + "Items");
            List var87 = var50.getStringList(var45 + "Potion-Effects");
            List var21 = var50.getStringList(var45 + "Abilities");
            List var22 = var50.getStringList(var45 + "Description");
            var50.set("Kits." + var40.getName(), (Object)null);
            var50.set("Kits." + var4[3] + ".Enabled", var54);
            var50.set("Kits." + var4[3] + ".Require-Permission", var58);
            var50.set("Kits." + var4[3] + ".Item", var62);
            var50.set("Kits." + var4[3] + ".Price", var66);
            var50.set("Kits." + var4[3] + ".Selection-Cooldown", var70);
            var50.set("Kits." + var4[3] + ".Armor.Helmet", var74);
            var50.set("Kits." + var4[3] + ".Armor.Chestplate", var78);
            var50.set("Kits." + var4[3] + ".Armor.Leggings", var80);
            var50.set("Kits." + var4[3] + ".Armor.Boots", var83);
            var50.set("Kits." + var4[3] + ".Items", var86);
            var50.set("Kits." + var4[3] + ".Abilities", var21);
            var50.set("Kits." + var4[3] + ".Potion-Effects", var87);
            var50.set("Kits." + var4[3] + ".Description", var22);
            var1.fileManager.saveConfig("kits.yml");
            ItemStack var23 = var40.getLogo().clone();
            ItemStack var24 = var40.getShopLogo().clone();
            var1.Kits.remove(var35);
            var40.setName(var4[3]);
            (new ItemStackBuilder(var40.getLogo())).setName(ChatColor.GREEN + var4[3]).build();
            (new ItemStackBuilder(var40.getShopLogo())).setName(ChatColor.LIGHT_PURPLE + var4[3]).build();
            var1.Kits.put(var4[3].toLowerCase(), var40);
            ItemStack var25 = var40.getLogo();
            var1.shop.updateItem(var24, var40.getShopLogo());

            for(PlayerData var27 : PlayerDataManager.getAll().values()) {
               for(int var28 = 0; var28 < var27.kitsInventory.getSize(); ++var28) {
                  for(int var32 : SmartInventory.smartSlots) {
                     if (var27.kitsInventory.getItem(var28, var32) != null && var27.kitsInventory.getItem(var28, var32).equals(var23)) {
                        var27.kitsInventory.setItem(var28, var32, var25);
                     }
                  }
               }
            }

            final String var88 = ChatColor.stripColor(var23.getItemMeta().getDisplayName());
            var3.sendMessage(var2.prefix + "The kit " + ChatColor.LIGHT_PURPLE + var88 + ChatColor.GRAY + " has been renamed to " + ChatColor.GREEN + var4[3] + ChatColor.GRAY + "!");
            (new BukkitRunnable() {
               final List<File> files = VexlioKitsAPI.getPlayersFiles();
               final Iterator<File> iterator;
               int updated;

               {
                  this.iterator = this.files.iterator();
                  this.updated = 0;
               }

               public void run() {
                  int var1 = 0;

                  while(var1 < 50 && this.iterator.hasNext()) {
                     ++var1;
                     File var2x = (File)this.iterator.next();
                     YamlConfiguration var3x = YamlConfiguration.loadConfiguration(var2x);
                     if (((FileConfiguration)var3x).getStringList("Kits").contains(var88)) {
                        List var4x = ((FileConfiguration)var3x).getStringList("Kits");
                        var4x.remove(var88);
                        var4x.add(var4[3]);
                        ((FileConfiguration)var3x).set("Kits", var4x);

                        try {
                           ((FileConfiguration)var3x).save(var2x);
                        } catch (IOException var6) {
                           var6.printStackTrace();
                        }

                        ++this.updated;
                     }
                  }

                  if (!this.iterator.hasNext()) {
                     var3.sendMessage(var2.prefix + "Updated the owned kits in the file for " + ChatColor.LIGHT_PURPLE + this.updated + ChatColor.GRAY + " players!");
                     this.cancel();
                  }

               }
            }).runTaskTimerAsynchronously(var1, 1L, 2L);
         } else if (var5.equals("enable")) {
            if (var4.length < 3) {
               var3.sendMessage(var2.prefix + "Usage: /vexliokits " + ChatColor.GREEN + "Kit" + ChatColor.GRAY + " enable <Kit>");
               return true;
            }

            Kit var36 = (Kit)var1.Kits.get(var4[2].toLowerCase());
            if (var36 == null) {
               var3.sendMessage(var2.prefix + "Couldn't find a kit with that name!");
               return true;
            }

            if (var36.enabled) {
               var3.sendMessage(var2.prefix + "This kit is already enabled!");
               return true;
            }

            var36.enabled = true;
            var1.fileManager.getConfig("kits.yml").set("Kits." + var36.getName() + ".Enabled", true);
            var1.fileManager.saveConfig("kits.yml");
            ItemStack var41 = var36.getLogo().clone();
            ItemStack var46 = var36.getShopLogo().clone();
            var36.generateLogos();
            var1.shop.updateItem(var46, var36.getShopLogo());

            for(PlayerData var55 : PlayerDataManager.getAll().values()) {
               for(int var59 = 0; var59 < var55.kitsInventory.getSize(); ++var59) {
                  for(int var75 : SmartInventory.smartSlots) {
                     if (var55.kitsInventory.getItem(var59, var75) != null && var55.kitsInventory.getItem(var59, var75).equals(var41)) {
                        var55.kitsInventory.setItem(var59, var75, var36.getLogo());
                     }
                  }
               }
            }

            var3.sendMessage(var2.prefix + "You have " + ChatColor.GREEN + "enabled" + ChatColor.GRAY + " the kit " + ChatColor.LIGHT_PURPLE + var36.getName());
         } else if (var5.equals("disable")) {
            if (var4.length < 3) {
               var3.sendMessage(var2.prefix + "Usage: /vexliokits " + ChatColor.GREEN + "Kit" + ChatColor.GRAY + " disable <Kit>");
               return true;
            }

            Kit var37 = (Kit)var1.Kits.get(var4[2].toLowerCase());
            if (var37 == null) {
               var3.sendMessage(var2.prefix + "Couldn't find a kit with that name!");
               return true;
            }

            if (!var37.enabled) {
               var3.sendMessage(var2.prefix + "This kit is already disabled!");
               return true;
            }

            var37.enabled = false;
            var1.fileManager.getConfig("kits.yml").set("Kits." + var37.getName() + ".Enabled", false);
            var1.fileManager.saveConfig("kits.yml");
            ItemStack var42 = var37.getLogo().clone();
            ItemStack var47 = var37.getShopLogo().clone();
            var37.generateLogos();
            var1.shop.updateItem(var47, var37.getShopLogo());

            for(PlayerData var56 : PlayerDataManager.getAll().values()) {
               for(int var60 = 0; var60 < var56.kitsInventory.getSize(); ++var60) {
                  for(int var76 : SmartInventory.smartSlots) {
                     if (var56.kitsInventory.getItem(var60, var76) != null && var56.kitsInventory.getItem(var60, var76).equals(var42)) {
                        var56.kitsInventory.setItem(var60, var76, var37.getLogo());
                     }
                  }
               }
            }

            var3.sendMessage(var2.prefix + "You have " + ChatColor.RED + "disabled" + ChatColor.GRAY + " the kit " + ChatColor.LIGHT_PURPLE + var37.getName());
         }

         return true;
      } else {
         return false;
      }
   }
}
