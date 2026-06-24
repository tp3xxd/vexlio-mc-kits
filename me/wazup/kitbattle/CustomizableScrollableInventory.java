package me.wazup.kitbattle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import me.wazup.kitbattle.managers.PlayerDataManager;
import me.wazup.kitbattle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class CustomizableScrollableInventory {
   private final String mainName;
   private LinkedHashMap<Inventory, HashMap<Integer, NavigationType>> inventories;

   public CustomizableScrollableInventory(String var1, FileConfiguration var2) {
      this.mainName = var1;
      this.inventories = new LinkedHashMap();

      for(String var4 : var2.getConfigurationSection("Pages").getKeys(false)) {
         String var5 = "Pages." + var4;
         int var6 = var2.getInt(var5 + ".Size");
         Inventory var7 = Bukkit.createInventory((InventoryHolder)null, var6, var1 + ": " + Utils.colorize(var4));
         HashMap var8 = new HashMap();

         for(String var10 : var2.getConfigurationSection(var5 + ".Design").getKeys(false)) {
            int var11 = Integer.parseInt(var10) - 1;
            ItemStack var12 = Utils.getItemStack(var2.getString(var5 + ".Design." + var10), false, true);
            var7.setItem(var11, var12);
            var8.put(var11, CustomizableScrollableInventory.NavigationType.NONE);
         }

         for(NavigationType var23 : CustomizableScrollableInventory.NavigationType.values()) {
            if (var2.contains(var5 + ".Navigation." + var23.name())) {
               for(String var14 : var2.getConfigurationSection(var5 + ".Navigation." + var23.name()).getKeys(false)) {
                  int var15 = Integer.parseInt(var14) - 1;
                  ItemStack var16 = Utils.getItemStack(var2.getString(var5 + ".Navigation." + var23.name() + "." + var14), false, true);
                  var7.setItem(var15, var16);
                  var8.put(var15, var23);
               }
            }
         }

         for(String var20 : var2.getConfigurationSection(var5 + ".Items").getKeys(false)) {
            int var22 = Integer.parseInt(var20) - 1;
            String var24 = var2.getString(var5 + ".Items." + var20);
            ItemStack var25 = this.getItem(var24);
            if (var25 != null) {
               var7.setItem(var22, var25);
               var8.remove(var22);
            }
         }

         this.inventories.put(var7, var8);
      }

   }

   public String getName() {
      return this.mainName;
   }

   public void open(Player var1) {
      var1.openInventory((Inventory)this.inventories.keySet().iterator().next());
   }

   public boolean handleClick(Player var1, Inventory var2, int var3) {
      HashMap var4 = (HashMap)this.inventories.get(var2);
      if (var4 == null) {
         return false;
      } else {
         NavigationType var5 = (NavigationType)var4.get(var3);
         if (var5 == null) {
            return false;
         } else {
            if (var5 == CustomizableScrollableInventory.NavigationType.NEXT_PAGE) {
               Iterator var6 = this.inventories.keySet().iterator();

               while(var6.hasNext()) {
                  Inventory var7 = (Inventory)var6.next();
                  if (var7.equals(var2)) {
                     if (var6.hasNext()) {
                        var1.openInventory((Inventory)var6.next());
                     }

                     return true;
                  }
               }
            } else if (var5 == CustomizableScrollableInventory.NavigationType.PREVIOUS_PAGE) {
               Inventory var9 = null;

               for(Inventory var8 : this.inventories.keySet()) {
                  if (var8.equals(var2)) {
                     break;
                  }

                  var9 = var8;
               }

               if (var9 != null) {
                  var1.openInventory(var9);
               }
            } else if (var5 == CustomizableScrollableInventory.NavigationType.UPGRADES) {
               PlayerDataManager.get(var1).openUpgrades(var1);
            }

            return true;
         }
      }
   }

   public void updateItem(ItemStack var1, ItemStack var2) {
      for(Inventory var4 : this.inventories.keySet()) {
         HashMap var5 = (HashMap)this.inventories.get(var4);

         for(int var6 = 0; var6 < var4.getSize(); ++var6) {
            if (!var5.containsKey(var6)) {
               ItemStack var7 = var4.getItem(var6);
               if (var7 != null && var7.equals(var1)) {
                  var4.setItem(var6, var2);
                  break;
               }
            }
         }
      }

   }

   public List<ItemStack> getAllContents() {
      ArrayList var1 = new ArrayList();

      for(Inventory var3 : this.inventories.keySet()) {
         HashMap var4 = (HashMap)this.inventories.get(var3);

         for(int var5 = 0; var5 < var3.getSize(); ++var5) {
            if (!var4.containsKey(var5)) {
               ItemStack var6 = var3.getItem(var5);
               if (var6 != null) {
                  var1.add(var6);
               }
            }
         }
      }

      return var1;
   }

   abstract ItemStack getItem(String var1);

   private static enum NavigationType {
      NONE,
      NEXT_PAGE,
      PREVIOUS_PAGE,
      UPGRADES;

      // $FF: synthetic method
      private static NavigationType[] $values() {
         return new NavigationType[]{NONE, NEXT_PAGE, PREVIOUS_PAGE, UPGRADES};
      }
   }
}
