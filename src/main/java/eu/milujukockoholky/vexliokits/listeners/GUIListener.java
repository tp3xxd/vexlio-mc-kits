package eu.milujukockoholky.vexliokits.listeners;

import java.util.Map;
import eu.milujukockoholky.vexliokits.Kit;
import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.events.PlayerSelectKitEvent;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
import eu.milujukockoholky.vexliokits.managers.SoundsManager;
import eu.milujukockoholky.vexliokits.managers.TitleManager;
import eu.milujukockoholky.vexliokits.utils.ItemStackBuilder;
import eu.milujukockoholky.vexliokits.utils.SmartInventory;
import eu.milujukockoholky.vexliokits.utils.Utils;
import eu.milujukockoholky.vexliokits.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {
   @EventHandler
   public void onPlayerClickInventory(InventoryClickEvent var1) {
      Player var2 = (Player)var1.getWhoClicked();
      if (VexlioKits.getInstance().players.contains(var2.getUniqueId())) {
         Inventory var3 = var1.getInventory();
         String var4 = var1.getView().getTitle();
         ItemStack var5 = var1.getCurrentItem();
         if (var4.contains((CharSequence)VexlioKits.getInstance().msgs.inventories.get("Kits"))) {
            PlayerData var22 = PlayerDataManager.get(var2);
            var1.setCancelled(true);
            if (var22.getKit() != null) {
               var2.sendMessage((String)Messages.getInstance().messages.get("Already-Selected-Kit"));
               var2.closeInventory();
            } else if (!var22.kitsInventory.handleClick(var2, var5, var3)) {
               if (var5 != null && !var5.getType().equals(Material.AIR)) {
                  Kit var26 = (Kit)VexlioKits.getInstance().Kits.get(ChatColor.stripColor(var5.getItemMeta().getDisplayName().toLowerCase()));
                  if (var26 != null) {
                     if (!var1.getAction().equals(InventoryAction.PICKUP_HALF)) {
                        if (!var26.isEnabled()) {
                           var2.sendMessage((String)VexlioKits.getInstance().msgs.messages.get("Kit-Disabled"));
                        } else if (var26.requirePermission && !var2.hasPermission(var26.permission)) {
                           var2.sendMessage((String)VexlioKits.getInstance().msgs.messages.get("No-Permission-For-Kit"));
                        } else if (!var22.hasKitSelectionCooldown(var2, var26.getName())) {
                           var22.setKitSelectionCooldown(var26.getName(), var26.selectionCooldown);
                           var22.setKit(var2, var26);
                           var26.giveItems(var2);
                           if (!TitleManager.getInstance().sendTitle(var2, ((String)VexlioKits.getInstance().msgs.titles.get("Kit-Select")).replace("%kit%", var26.getName()))) {
                              var2.sendMessage(((String)VexlioKits.getInstance().msgs.messages.get("Player-Select-Kit")).replace("%kit%", var26.getName()));
                           }

                           var2.closeInventory();
                           Bukkit.getPluginManager().callEvent(new PlayerSelectKitEvent(var2, var26));
                        }
                     } else if (!var22.hasCooldown(var2, "Selling")) {
                        var22.setCooldown(var2, "Selling", 3, false);

                        Kit var28;
                        for(var28 = var26; var28.original != null; var28 = var28.original) {
                        }

                        if (!VexlioKits.getInstance().config.defaultKits.contains(var28.getName().toLowerCase())) {
                           Inventory var30 = Bukkit.createInventory(var2, 9, (String)VexlioKits.getInstance().msgs.inventories.get("Selling"));
                           var30.setItem(2, VexlioKits.getInstance().confirm_itemstack);
                           ItemStack var32 = (new ItemStackBuilder(var5.clone())).addLore(" ", ChatColor.RED + "Sell value: " + ChatColor.YELLOW + (int)((double)var26.getTotalPrice() * VexlioKits.getInstance().config.SellValue) + "$").build();
                           var30.setItem(4, var32);
                           var30.setItem(6, VexlioKits.getInstance().cancel_itemstack);
                           var2.openInventory(var30);
                        }
                     }
                  }
               }
            }
         } else if (var4.equals(VexlioKits.getInstance().msgs.inventories.get("Map-Vote"))) {
            var1.setCancelled(true);
            PlayerData var21 = PlayerDataManager.get(var2);
            if (var5 != null && !var5.getType().equals(Material.AIR) && var5.getType().equals(Material.NAME_TAG) && !var21.hasCooldown(var2, "Vote")) {
               var21.setCooldown(var2, "Vote", 1, false);
               VexlioKits.getInstance().bungeeMode.vote(var2, var5);
            }
         } else if (var4.equals(VexlioKits.getInstance().msgs.inventories.get("Profile-Inventory"))) {
            var1.setCancelled(true);
            if (var5 != null && !var5.getType().equals(Material.AIR)) {
               if (var5.getType().equals(Material.PAPER)) {
                  var2.openInventory(PlayerDataManager.get(var2).getStatsInventory(var2));
               } else if (var5.getType().equals(Material.ENDER_CHEST)) {
                  PlayerDataManager.get(var2).achievements.open(var2);
               }

            }
         } else if (var4.equals(VexlioKits.getInstance().msgs.inventories.get("Trails-Inventory"))) {
            var1.setCancelled(true);
            if (var1.getRawSlot() <= 53 && var5 != null && !var5.getType().equals(Material.AIR)) {
               if (var5.getType().equals(Material.ARROW)) {
                  PlayerDataManager.get(var2).selectedTrail = null;
               } else {
                  PlayerDataManager.get(var2).selectedTrail = Effect.valueOf(ChatColor.stripColor(var5.getItemMeta().getDisplayName()));
               }

               var2.sendMessage(((String)VexlioKits.getInstance().msgs.messages.get("Trail-Select")).replace("%trail%", var5.getItemMeta().getDisplayName()));
            }
         } else if (!var4.equals(VexlioKits.getInstance().msgs.inventories.get("Stats-Inventory")) && !var4.contains((CharSequence)VexlioKits.getInstance().msgs.inventories.get("Achievements-Inventory"))) {
            if (var4.contains(VexlioKits.getInstance().shop.getName())) {
               var1.setCancelled(true);
               if (var5 != null && !var5.getType().equals(Material.AIR)) {
                  if (var4.contains("Upgrades")) {
                     PlayerData var19 = PlayerDataManager.get(var2);
                     if (var19.upgradesInventory != null) {
                        if (var19.upgradesInventory.handleClick(var2, var5, var3)) {
                           return;
                        }
                     } else {
                        var2.closeInventory();
                     }

                     if (Utils.compareItem(var5, VexlioKits.getInstance().back_itemstack)) {
                        VexlioKits.getInstance().shop.open(var2);
                        return;
                     }
                  } else if (VexlioKits.getInstance().shop.handleClick(var2, var3, var1.getRawSlot())) {
                     return;
                  }

                  Kit var20 = (Kit)VexlioKits.getInstance().Kits.get(ChatColor.stripColor(var5.getItemMeta().getDisplayName().toLowerCase()));
                  if (var20 != null) {
                     if (var1.getAction().equals(InventoryAction.PICKUP_HALF)) {
                        var2.openInventory(var20.kitPreview);
                     } else if (!var20.isEnabled()) {
                        var2.sendMessage((String)VexlioKits.getInstance().msgs.messages.get("Kit-Disabled"));
                     } else if (var20.requirePermission && !var2.hasPermission(var20.permission)) {
                        var2.sendMessage((String)VexlioKits.getInstance().msgs.messages.get("No-Permission-For-Kit"));
                     } else {
                        if (PlayerDataManager.get(var2).getCoins(var2) >= var20.getPrice()) {
                           Inventory var25 = Bukkit.createInventory(var2, 9, ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "Are you sure?");
                           var25.setItem(4, var5);
                           var25.setItem(2, VexlioKits.getInstance().confirm_itemstack);
                           var25.setItem(6, VexlioKits.getInstance().cancel_itemstack);
                           var2.openInventory(var25);
                        } else {
                           var2.sendMessage((String)VexlioKits.getInstance().msgs.messages.get("Not-Enough-Coins"));
                        }

                     }
                  }
               }
            } else {
               if (var4.equals(VexlioKits.getInstance().msgs.inventories.get("Queue"))) {
                  var1.setCancelled(true);
                  if (VexlioKits.getInstance().challengesManager == null) {
                     var2.closeInventory();
                  }

                  if (var1.getRawSlot() < -1 || var1.getRawSlot() > var1.getInventory().getSize() || var5 == null || var5.getType().equals(Material.AIR)) {
                     return;
                  }

                  PlayerData var6 = PlayerDataManager.get(var2);
                  if (var6.hasCooldown(var2, "Challenge_Queue")) {
                     return;
                  }

                  var6.setCooldown(var2, "Challenge_Queue", 3, false);
                  int var7 = Integer.parseInt(String.valueOf(ChatColor.stripColor(var5.getItemMeta().getDisplayName()).charAt(0)));
                  if (VexlioKits.getInstance().challengesManager.isInQueue(var2, var7)) {
                     VexlioKits.getInstance().challengesManager.remove(var2, var7);
                     var3.setItem(var1.getRawSlot(), (new ItemStackBuilder(XMaterial.GRAY_DYE.parseItem())).setName(ChatColor.AQUA + "" + var7 + "v" + var7).build());
                     var2.playSound(var2.getLocation(), SoundsManager.CLICK, 1.0F, 1.0F);
                  } else {
                     if (var1.getAction().equals(InventoryAction.PICKUP_HALF)) {
                        VexlioKits.getInstance().challengesManager.add(var2, var7, true);
                        var3.setItem(var1.getRawSlot(), (new ItemStackBuilder(XMaterial.PINK_DYE.parseItem())).setName(ChatColor.AQUA + "" + var7 + "v" + var7).build());
                     } else {
                        VexlioKits.getInstance().challengesManager.add(var2, var7, false);
                        var3.setItem(var1.getRawSlot(), (new ItemStackBuilder(XMaterial.LIME_DYE.parseItem())).setName(ChatColor.AQUA + "" + var7 + "v" + var7).build());
                     }

                     var2.playSound(var2.getLocation(), SoundsManager.CLICK, 1.0F, 1.0F);
                  }
               }

               if (var4.contains((CharSequence)VexlioKits.getInstance().msgs.inventories.get("Kit-Preview"))) {
                  var1.setCancelled(true);
                  if (var5 != null && !var5.getType().equals(Material.AIR) && !Utils.compareItem(var5, VexlioKits.getInstance().pane_itemstack) && var1.getRawSlot() >= -1 && var1.getRawSlot() <= var1.getInventory().getSize()) {
                     if (Utils.compareItem(var5, VexlioKits.getInstance().back_itemstack)) {
                        String var18 = ChatColor.stripColor(var4.split(": ")[1]).toLowerCase();
                        if (VexlioKits.getInstance().Kits.containsKey(var18) && ((Kit)VexlioKits.getInstance().Kits.get(var18)).original != null) {
                           PlayerDataManager.get(var2).openUpgrades(var2);
                        } else {
                           VexlioKits.getInstance().shop.open(var2);
                        }
                     }

                  }
               } else if (var4.equals(ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "Are you sure?")) {
                  var1.setCancelled(true);
                  if (var5 != null && !var5.getType().equals(Material.AIR)) {
                     String var17 = ChatColor.stripColor(var3.getItem(4).getItemMeta().getDisplayName().toLowerCase());
                     Kit var24 = (Kit)VexlioKits.getInstance().Kits.get(var17);
                     if (var24 != null) {
                        PlayerData var27 = PlayerDataManager.get(var2);
                        if (var5.equals(VexlioKits.getInstance().cancel_itemstack)) {
                           if (var24.original != null) {
                              var27.openUpgrades(var2);
                           } else {
                              VexlioKits.getInstance().shop.open(var2);
                           }

                        } else {
                           if (var5.equals(VexlioKits.getInstance().confirm_itemstack)) {
                              if (VexlioKits.getInstance().config.PurchaseableKitsArePermanent) {
                                 if (var27.kitsInventory.getAllContents().contains(var24.getLogo())) {
                                    var2.sendMessage((String)VexlioKits.getInstance().msgs.messages.get("Kit-Already-Unlocked"));
                                    return;
                                 }

                                 int var29 = 0;
                                 int var31 = 0;
                                 if (var24.original == null) {
                                    Map.Entry var33 = var27.kitsInventory.getEmptySlot();
                                    var31 = (Integer)var33.getKey();
                                    var29 = (Integer)var33.getValue();
                                 } else {
                                    for(int var34 = 0; var34 < var27.kitsInventory.getSize(); ++var34) {
                                       for(int var15 : SmartInventory.smartSlots) {
                                          if (var27.kitsInventory.getItem(var34, var15) != null && var27.kitsInventory.getItem(var34, var15).equals(var24.original.getLogo())) {
                                             var29 = var15;
                                             var31 = var34;
                                             break;
                                          }
                                       }
                                    }
                                 }

                                 var27.kitsInventory.setItem(var31, var29, var24.getLogo());
                                 if (var24.original == null) {
                                    VexlioKits.getInstance().shop.open(var2);
                                 } else {
                                    var2.closeInventory();
                                 }
                              } else {
                                 var2.closeInventory();
                                 var24.giveItems(var2);
                                 var27.setKit(var2, var24);
                                 var2.sendMessage(((String)VexlioKits.getInstance().msgs.messages.get("Player-Select-Kit")).replace("%kit%", var24.getName()));
                              }

                              var27.removeCoins(var2, var24.getPrice());
                              if (var27.customScoreboard != null) {
                                 var27.customScoreboard.updatePlaceholder("%coins%", var27.getCoins(var2));
                              }

                              var2.sendMessage(((String)VexlioKits.getInstance().msgs.messages.get("Player-Purchase-Kit")).replace("%kit%", var24.getName()).replace("%price%", String.valueOf(var24.getPrice())));
                           }

                        }
                     }
                  }
               } else if (!var4.equals(VexlioKits.getInstance().msgs.inventories.get("Selling"))) {
                  if (var4.equals(VexlioKits.getInstance().msgs.inventories.get("Kit-Unlocker"))) {
                     var1.setCancelled(true);
                  }
               } else {
                  var1.setCancelled(true);
                  if (var5 != null && !var5.getType().equals(Material.AIR)) {
                     if (Utils.compareItem(var5, VexlioKits.getInstance().cancel_itemstack)) {
                        PlayerDataManager.get(var2).kitsInventory.open(var2);
                     } else if (Utils.compareItem(var5, VexlioKits.getInstance().confirm_itemstack)) {
                        Kit var16 = (Kit)VexlioKits.getInstance().Kits.get(ChatColor.stripColor(var3.getItem(4).getItemMeta().getDisplayName().toLowerCase()));
                        if (var16 == null) {
                           var2.closeInventory();
                        } else {
                           PlayerData var23 = PlayerDataManager.get(var2);

                           for(int var8 = 0; var8 < var23.kitsInventory.getSize(); ++var8) {
                              for(int var12 : SmartInventory.smartSlots) {
                                 if (var23.kitsInventory.getItem(var8, var12) != null && var23.kitsInventory.getItem(var8, var12).getItemMeta().getDisplayName().equals(var16.getLogo().getItemMeta().getDisplayName())) {
                                    var23.kitsInventory.removeItem(var8, var12);
                                    var23.addCoins(var2, (int)((double)var16.getTotalPrice() * VexlioKits.getInstance().config.SellValue));
                                    if (var23.customScoreboard != null) {
                                       var23.customScoreboard.updatePlaceholder("%coins%", var23.getCoins(var2));
                                    }

                                    var23.kitsInventory.open(var2);
                                    return;
                                 }

                                 var2.closeInventory();
                              }
                           }

                        }
                     }
                  }
               }
            }
         } else {
            var1.setCancelled(true);
            if (var5 != null && !var5.getType().equals(Material.AIR)) {
               if (Utils.compareItem(var5, VexlioKits.getInstance().back_itemstack)) {
                  if (VexlioKits.getInstance().achievementsManager.enabled) {
                     var2.openInventory(VexlioKits.getInstance().profileInventory);
                  } else {
                     var2.closeInventory();
                  }
               } else if (var4.contains((CharSequence)VexlioKits.getInstance().msgs.inventories.get("Achievements-Inventory"))) {
                  PlayerDataManager.get(var2).achievements.handleClick(var2, var5, var3);
               }

            }
         }
      }
   }
}
