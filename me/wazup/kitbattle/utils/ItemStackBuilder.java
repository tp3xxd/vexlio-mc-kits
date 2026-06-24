package me.wazup.kitbattle.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.wazup.kitbattle.Kitbattle;
import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class ItemStackBuilder {
   private static Method setDamageMethod;
   private static Method setUnbreakableOld;
   private static Method getItemSpigot;
   private static Method setUnbreakableNew;
   HashMap<String, Integer> customEnchants;
   private final ItemStack item;
   private ItemMeta meta;
   private final List<String> lore;

   public ItemStackBuilder(ItemStack var1) {
      this.item = var1;
      this.meta = var1.getItemMeta();
      this.lore = (List<String>)(this.meta != null && this.meta.hasLore() ? this.meta.getLore() : new ArrayList());
   }

   public ItemStackBuilder(Material var1) {
      this(new ItemStack(var1));
   }

   public static void loadMethods() {
      try {
         for(Method var3 : Class.forName("org.bukkit.inventory.meta.Damageable").getMethods()) {
            if (var3.getName().equals("setDamage")) {
               setDamageMethod = var3;
               break;
            }
         }
      } catch (Exception var6) {
      }

      try {
         setUnbreakableNew = ItemMeta.class.getMethod("setUnbreakable", Boolean.TYPE);
      } catch (Exception var5) {
         try {
            setUnbreakableOld = Class.forName("org.bukkit.inventory.meta.ItemMeta.Spigot").getMethod("setUnbreakable", Boolean.TYPE);
            getItemSpigot = ItemMeta.class.getMethod("spigot");
         } catch (Exception var4) {
         }
      }

   }

   public ItemStackBuilder setType(Material var1) {
      this.item.setType(var1);
      return this;
   }

   public ItemStackBuilder setName(String var1) {
      this.meta.setDisplayName(var1);
      return this;
   }

   public ItemStackBuilder addLore(String... var1) {
      for(String var5 : var1) {
         this.lore.add(var5);
      }

      return this;
   }

   public ItemStackBuilder addEnchantment(String var1, int var2) {
      if (Kitbattle.getInstance().AdvancedEnchantmentsEnabled && AEAPI.isAnEnchantment(var1)) {
         if (this.customEnchants == null) {
            this.customEnchants = new HashMap();
         }

         this.customEnchants.put(var1, var2);
      } else {
         this.meta.addEnchant(Enchantment.getByName(var1), var2, true);
      }

      return this;
   }

   public ItemStackBuilder setDurability(int var1) {
      if (setDamageMethod != null) {
         try {
            setDamageMethod.invoke(this.meta, (short)var1);
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      } else {
         this.item.setDurability((short)var1);
      }

      return this;
   }

   public ItemStackBuilder setAmount(int var1) {
      this.item.setAmount(var1);
      return this;
   }

   public void setCustomModelData(Integer var1) {
      this.meta.setCustomModelData(var1);
   }

   public ItemStackBuilder clearLore() {
      this.lore.clear();
      return this;
   }

   public ItemStackBuilder removeLastLore() {
      this.lore.remove(this.lore.size() - 1);
      return this;
   }

   public void setColor(Color var1) {
      ((LeatherArmorMeta)this.meta).setColor(var1);
   }

   public void setPotionEffect(PotionType var1, boolean var2, boolean var3) {
      PotionMeta var4 = (PotionMeta)this.item.getItemMeta();

      try {
         PotionMeta.class.getMethod("setBasePotionData", Class.forName("org.bukkit.potion.PotionData")).invoke(var4, Class.forName("org.bukkit.potion.PotionData").getConstructor(PotionType.class, Boolean.TYPE, Boolean.TYPE).newInstance(var1, var2, var3));
      } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException var6) {
         Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[KitBattle] The used format for potions does not work on this minecraft version, please try other format which is POTION:DURABILITY");
      }

      this.meta = var4;
   }

   public ItemStackBuilder replaceLore(String var1, String var2) {
      for(int var3 = 0; var3 < this.lore.size(); ++var3) {
         if (((String)this.lore.get(var3)).contains(var1)) {
            this.lore.remove(var3);
            this.lore.add(var3, var2);
            break;
         }
      }

      return this;
   }

   public ItemStackBuilder tagUnbreakable() {
      try {
         if (getItemSpigot != null) {
            Object var1 = getItemSpigot.invoke(this.meta);
            setUnbreakableOld.invoke(var1, true);
         } else if (setUnbreakableNew != null) {
            setUnbreakableNew.invoke(this.meta, true);
         } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[KitBattle] The unbreakable tag is not useable for this minecraft server version!");
         }
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var2) {
         Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[KitBattle] The unbreakable tag is not useable for this minecraft server version!");
      }

      return this;
   }

   public ItemStack build() {
      if (this.meta != null) {
         this.meta.setLore(this.lore);
      }

      this.lore.clear();
      this.item.setItemMeta(this.meta);
      if (this.customEnchants != null) {
         for(String var2 : this.customEnchants.keySet()) {
            AEAPI.applyEnchant(var2, (Integer)this.customEnchants.get(var2), this.item);
         }
      }

      return this.item;
   }
}
