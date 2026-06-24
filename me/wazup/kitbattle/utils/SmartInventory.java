package me.wazup.kitbattle.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.wazup.kitbattle.Kitbattle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class SmartInventory {
   public static final int[] smartSlots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
   private final String mainName;
   private final HashMap<Integer, Inventory> inventories = new HashMap();
   private final Kitbattle plugin;

   public SmartInventory(Kitbattle var1, String var2) {
      this.plugin = var1;
      this.mainName = var2 + ": ";
   }

   public int addInventory(String var1) {
      int var2 = this.inventories.size();
      Inventory var3 = Bukkit.createInventory((InventoryHolder)null, 54, this.mainName + var1);
      this.plugin.cageInventory(var3, false);
      if (var2 != 0) {
         var3.setItem(18, this.plugin.previous_itemstack);
         var3.setItem(27, this.plugin.previous_itemstack);
         Inventory var4 = (Inventory)this.inventories.get(var2 - 1);
         var4.setItem(26, this.plugin.next_itemstack);
         var4.setItem(35, this.plugin.next_itemstack);
      }

      this.inventories.put(var2, var3);
      return var2;
   }

   public void setItem(int var1, int var2, ItemStack var3) {
      ((Inventory)this.inventories.get(var1)).setItem(var2, var3);
   }

   public void removeItem(int var1, int var2) {
      this.setItem(var1, var2, new ItemStack(Material.AIR));
      this.organizeAll();
   }

   public ItemStack getItem(int var1, int var2) {
      return ((Inventory)this.inventories.get(var1)).getItem(var2);
   }

   public void organizeAll() {
      ArrayList var1 = new ArrayList();

      for(int var3 : this.inventories.keySet()) {
         Inventory var4 = (Inventory)this.inventories.get(var3);

         for(int var8 : smartSlots) {
            ItemStack var9 = var4.getItem(var8);
            if (var9 != null) {
               var1.add(var9);
               var4.setItem(var8, new ItemStack(Material.AIR));
            }
         }
      }

      Iterator var10 = var1.iterator();

      for(int var11 = 0; (double)var11 < Math.ceil((double)var1.size() / (double)smartSlots.length); ++var11) {
         for(int var15 : smartSlots) {
            if (!var10.hasNext()) {
               break;
            }

            ItemStack var16 = (ItemStack)var10.next();
            this.setItem(var11, var15, var16);
         }
      }

   }

   public int getEmptySlot(int var1) {
      Inventory var2 = (Inventory)this.inventories.get(var1);

      for(int var6 : smartSlots) {
         if (var2.getItem(var6) == null) {
            return var6;
         }
      }

      return -1;
   }

   public Map.Entry<Integer, Integer> getEmptySlot() {
      int var1 = -1;
      int var2 = 0;

      for(int var3 = 0; var3 < this.inventories.size(); ++var3) {
         if ((var1 = this.getEmptySlot(var3)) > -1) {
            var2 = var3;
            break;
         }
      }

      if (var1 == -1) {
         var2 = this.addInventory(ChatColor.RED + "List #" + (this.inventories.size() + 1));
         var1 = smartSlots[0];
      }

      return new AbstractMap.SimpleEntry(var2, var1);
   }

   public List<ItemStack> getContents(int var1) {
      ArrayList var2 = new ArrayList();
      Inventory var3 = (Inventory)this.inventories.get(var1);

      for(int var7 : smartSlots) {
         ItemStack var8 = var3.getItem(var7);
         if (var8 != null) {
            var2.add(var8);
         }
      }

      return var2;
   }

   public boolean contains(ItemStack var1) {
      for(int var2 = 0; var2 < this.inventories.size(); ++var2) {
         if (this.getContents(var2).contains(var1)) {
            return true;
         }
      }

      return false;
   }

   public List<ItemStack> getAllContents() {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < this.inventories.size(); ++var2) {
         var1.addAll(this.getContents(var2));
      }

      return var1;
   }

   public boolean handleClick(Player var1, ItemStack var2, Inventory var3) {
      if (!Utils.compareItem(var2, this.plugin.next_itemstack) && !Utils.compareItem(var2, this.plugin.previous_itemstack)) {
         return false;
      } else {
         int var4 = 0;

         for(int var5 = 0; var5 < this.inventories.size(); ++var5) {
            var4 = var5;
            if (((Inventory)this.inventories.get(var5)).equals(var3)) {
               break;
            }
         }

         if (Utils.compareItem(var2, this.plugin.next_itemstack)) {
            var1.openInventory((Inventory)this.inventories.get(var4 + 1));
         } else {
            var1.openInventory((Inventory)this.inventories.get(var4 - 1));
         }

         return true;
      }
   }

   public int getSize() {
      return this.inventories.size();
   }

   public String getName() {
      return this.mainName;
   }

   public void open(Player var1) {
      var1.openInventory((Inventory)this.inventories.get(0));
   }
}
