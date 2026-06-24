package me.wazup.kitbattle.utils;

import me.wazup.kitbattle.Config;
import me.wazup.kitbattle.Kitbattle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtils {
   public static void applyHotbarItem(Player var0, String var1) {
      applyHotbarItem(var0, var1, 1);
   }

   public static void applyHotbarItem(Player var0, String var1, int var2) {
      Config.HotbarItem var3 = (Config.HotbarItem)Kitbattle.getInstance().config.hotBarItems.get(var1);
      if (var3.enabled) {
         if (var2 > 1) {
            ItemStack var4 = var3.item.clone();
            var4.setAmount(var2);
            var0.getInventory().setItem(var3.slot, var4);
         } else {
            var0.getInventory().setItem(var3.slot, var3.item);
         }

      }
   }
}
