package eu.milujukockoholky.vexliokits;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import eu.milujukockoholky.vexliokits.abilities.AbilityManager;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
import eu.milujukockoholky.vexliokits.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

public class Kit {
   Collection<PotionEffect> effects;
   List<String> commands;
   public int selectionCooldown;
   ArrayList<String> abilitiesWritten;
   ArrayList<Ability> interactionAbilities;
   ArrayList<Ability> attackAbilities;
   ArrayList<Ability> attackReceiveAbilities;
   ArrayList<Ability> damageAbilities;
   ArrayList<Ability> projectileAbilities;
   ArrayList<Ability> entityInteractionAbilities;
   ArrayList<Ability> otherAbilities;
   public boolean enabled;
   public boolean requirePermission;
   public String name;
   public String permission;
   public Inventory kitPreview;
   public Kit original;
   private final ItemStack originalLogo;
   private ItemStack logo;
   private ItemStack shopLogo;
   private final ItemStack[] items;
   private final ItemStack[] armor;
   private final int price;
   private final VexlioKits plugin;

   public Kit(VexlioKits var1, String var2, ItemStack var3, ItemStack[] var4, ItemStack[] var5, int var6, int var7, Collection<PotionEffect> var8, ArrayList<String> var9, boolean var10, boolean var11, List<String> var12) {
      this.plugin = var1;
      this.originalLogo = var3;
      this.items = var4;
      this.armor = var5;
      this.price = var6;
      this.selectionCooldown = var7;
      this.name = var2;
      this.effects = var8;
      this.abilitiesWritten = var9;
      this.enabled = var10;
      this.requirePermission = var11;
      this.commands = var12;
      this.permission = "VexlioKits.kits." + var2;
      this.loadAbilities();
      this.generateLogos();
      this.kitPreview = Bukkit.createInventory((InventoryHolder)null, 45, (String)var1.msgs.inventories.get("Kit-Preview") + var2);

      for(int var13 = 0; var13 < var4.length; ++var13) {
         if (var4[var13] != null && !var4[var13].getType().equals(Material.AIR)) {
            this.kitPreview.setItem(var13 + 9 >= 45 ? 44 : var13 + 9, var4[var13]);
         }
      }

      for(int var14 = 4; var14 < 8; ++var14) {
         this.kitPreview.setItem(var14, var1.pane_itemstack);
      }

      for(int var15 = 0; var15 < var5.length; ++var15) {
         if (var5[var15] != null) {
            this.kitPreview.addItem(new ItemStack[]{var5[var15]});
         }
      }

      this.kitPreview.setItem(8, var1.back_itemstack);
   }

   public ItemStack getLogo() {
      return this.logo;
   }

   public ItemStack getShopLogo() {
      return this.shopLogo;
   }

   public void generateLogos() {
      this.logo = this.originalLogo.clone();
      this.shopLogo = this.logo.clone();
      ItemMeta var1 = this.logo.getItemMeta();
      Object var2 = new ArrayList();
      if (var1.getLore() != null) {
         var2 = var1.getLore();
      }

      for(String var4 : this.plugin.config.kitLoresOwned) {
         ((List)var2).add(Utils.colorize(var4.replace("%statecolor%", this.enabled ? ChatColor.GREEN.toString() : ChatColor.RED.toString()).replace("%state%", this.enabled ? "Enabled" : "Disabled").replace("%permissioncolor%", this.requirePermission ? ChatColor.RED.toString() : ChatColor.GREEN.toString()).replace("%requirespermission%", this.requirePermission ? "Yes" : "No").replace("%price%", String.valueOf(this.price))));
      }

      var1.setLore((List)var2);
      this.logo.setItemMeta(var1);
      ItemMeta var7 = this.shopLogo.getItemMeta();
      var7.setDisplayName(ChatColor.LIGHT_PURPLE + this.name);
      Object var8 = new ArrayList();
      if (var7.getLore() != null) {
         var8 = var7.getLore();
      }

      for(String var6 : this.plugin.config.kitLoresShop) {
         ((List)var8).add(Utils.colorize(var6.replace("%statecolor%", this.enabled ? ChatColor.GREEN.toString() : ChatColor.RED.toString()).replace("%state%", this.enabled ? "Enabled" : "Disabled").replace("%permissioncolor%", this.requirePermission ? ChatColor.RED.toString() : ChatColor.GREEN.toString()).replace("%requirespermission%", this.requirePermission ? "Yes" : "No").replace("%price%", String.valueOf(this.price))));
      }

      var7.setLore((List)var8);
      this.shopLogo.setItemMeta(var7);
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public ItemStack[] getItems() {
      return this.items;
   }

   public ItemStack[] getArmor() {
      return this.armor;
   }

   public ArrayList<Ability> getInteractionAbilities() {
      return this.interactionAbilities;
   }

   public ArrayList<Ability> getAttackAbilities() {
      return this.attackAbilities;
   }

   public ArrayList<Ability> getAttackReceiveAbilities() {
      return this.attackReceiveAbilities;
   }

   public ArrayList<Ability> getDamageAbilities() {
      return this.damageAbilities;
   }

   public ArrayList<Ability> getProjectileAbilities() {
      return this.projectileAbilities;
   }

   public ArrayList<Ability> getEntityInteractionAbilities() {
      return this.entityInteractionAbilities;
   }

   public ArrayList<Ability> getOtherAbilities() {
      return this.otherAbilities;
   }

   public void loadAbilities() {
      this.interactionAbilities = new ArrayList();
      this.attackAbilities = new ArrayList();
      this.attackReceiveAbilities = new ArrayList();
      this.damageAbilities = new ArrayList();
      this.projectileAbilities = new ArrayList();
      this.entityInteractionAbilities = new ArrayList();
      this.otherAbilities = new ArrayList();
      if (!this.abilitiesWritten.isEmpty()) {
         for(String var2 : this.abilitiesWritten) {
            if (AbilityManager.getInstance().abilities.containsKey(var2)) {
               Ability var3 = (Ability)AbilityManager.getInstance().abilities.get(var2);
               if (var3.getActivationMaterial() != null) {
                  this.interactionAbilities.add(var3);
               }

               if (var3.isDamageActivated()) {
                  this.damageAbilities.add(var3);
               }

               if (var3.isAttackActivated()) {
                  this.attackAbilities.add(var3);
               }

               if (var3.isAttackReceiveActivated()) {
                  this.attackReceiveAbilities.add(var3);
               }

               if (var3.getActivationProjectile() != null) {
                  this.projectileAbilities.add(var3);
               }

               if (var3.isEntityInteractionActivated()) {
                  this.entityInteractionAbilities.add(var3);
               }

               if (!this.interactionAbilities.contains(var3) && !this.attackAbilities.contains(var3) && !this.attackReceiveAbilities.contains(var3) && !this.damageAbilities.contains(var3) && !this.projectileAbilities.contains(var3) && !this.entityInteractionAbilities.contains(var3)) {
                  this.otherAbilities.add(var3);
               }
            }
         }
      }

   }

   public void giveItems(Player var1) {
      var1.getInventory().setContents(this.items);
      var1.getInventory().setArmorContents(this.armor);
      var1.addPotionEffects(this.effects);

      for(String var3 : this.commands) {
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var3.replace("%player%", var1.getName()));
      }

      PlayerData var6 = PlayerDataManager.get(var1);
      if (var6.deathstreak >= this.plugin.config.leastDeathstreak) {
         int var7 = this.plugin.config.leastDeathstreak;

         for(int var5 : this.plugin.config.Deathstreaks.keySet()) {
            if (var6.deathstreak >= var5) {
               var7 = var5;
            }
         }

         for(String var9 : (List<String>)this.plugin.config.Deathstreaks.get(var7)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var9.replace("%player%", var1.getName()));
         }
      }

   }

   public int getPrice() {
      return this.price;
   }

   public int getTotalPrice() {
      int var1 = this.price;

      for(Kit var2 = this.original; var2 != null; var2 = var2.original) {
         var1 += var2.getPrice();
      }

      return var1;
   }
}
