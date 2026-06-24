package me.wazup.kitbattle.utils;

import com.google.common.collect.Lists;
import dev.lone.itemsadder.api.CustomStack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.wazup.kitbattle.Kitbattle;
import me.zombie_striker.qg.api.QualityArmory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionType;

public class Utils {
   public static final Random random = new Random();
   private static final ChatColor[] goodColors;
   private static final Pattern HEX_PATTERN;

   public static void error(String var0) {
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + var0);
   }

   public static ArrayList<Block> getCageBlocks(Location var0) {
      ArrayList var1 = new ArrayList();
      var1.add(var0.clone().add((double)0.0F, (double)-1.0F, (double)0.0F).getBlock());
      var1.add(var0.clone().add((double)-1.0F, (double)0.0F, (double)0.0F).getBlock());
      var1.add(var0.clone().add((double)0.0F, (double)0.0F, (double)1.0F).getBlock());
      var1.add(var0.clone().add((double)0.0F, (double)0.0F, (double)-1.0F).getBlock());
      var1.add(var0.clone().add((double)1.0F, (double)0.0F, (double)0.0F).getBlock());
      var1.add(var0.clone().add((double)-1.0F, (double)0.0F, (double)-1.0F).getBlock());
      var1.add(var0.clone().add((double)-1.0F, (double)0.0F, (double)1.0F).getBlock());
      var1.add(var0.clone().add((double)1.0F, (double)0.0F, (double)-1.0F).getBlock());
      var1.add(var0.clone().add((double)1.0F, (double)0.0F, (double)1.0F).getBlock());
      var1.add(var0.clone().add((double)0.0F, (double)2.0F, (double)0.0F).getBlock());
      var1.add(var0.getBlock());
      var1.add(var0.add((double)0.0F, (double)1.0F, (double)0.0F).getBlock());
      return var1;
   }

   public static ArrayList<Location> getPlatForm(Location var0) {
      ArrayList var1 = new ArrayList();
      var1.add(var0.clone());
      var1.add(var0.clone().add((double)-1.0F, (double)0.0F, (double)0.0F));
      var1.add(var0.clone().add((double)0.0F, (double)0.0F, (double)-1.0F));
      var1.add(var0.clone().add((double)1.0F, (double)0.0F, (double)0.0F));
      var1.add(var0.clone().add((double)0.0F, (double)0.0F, (double)1.0F));
      var1.add(var0.clone().add((double)-1.0F, (double)0.0F, (double)-1.0F));
      var1.add(var0.clone().add((double)1.0F, (double)0.0F, (double)-1.0F));
      var1.add(var0.clone().add((double)1.0F, (double)0.0F, (double)1.0F));
      var1.add(var0.clone().add((double)-1.0F, (double)0.0F, (double)1.0F));
      return var1;
   }

   public static ArrayList<Location> getRoomLocations(Location var0) {
      ArrayList var1 = new ArrayList();
      var0.add((double)0.0F, (double)9.0F, (double)0.0F);
      var1.addAll(getPlatForm(var0));

      for(int var2 = 0; var2 < 3; ++var2) {
         var0.add((double)0.0F, (double)1.0F, (double)0.0F);
         var1.add(var0.clone().add((double)0.0F, (double)0.0F, (double)-2.0F));
         var1.add(var0.clone().add((double)0.0F, (double)0.0F, (double)2.0F));
         var1.add(var0.clone().add((double)2.0F, (double)0.0F, (double)0.0F));
         var1.add(var0.clone().add((double)-2.0F, (double)0.0F, (double)0.0F));
         var1.add(var0.clone().add((double)-2.0F, (double)0.0F, (double)2.0F));
         var1.add(var0.clone().add((double)-2.0F, (double)0.0F, (double)-2.0F));
         var1.add(var0.clone().add((double)2.0F, (double)0.0F, (double)-2.0F));
         var1.add(var0.clone().add((double)2.0F, (double)0.0F, (double)2.0F));
         var1.add(var0.clone().add((double)1.0F, (double)0.0F, (double)2.0F));
         var1.add(var0.clone().add((double)-1.0F, (double)0.0F, (double)2.0F));
         var1.add(var0.clone().add((double)-2.0F, (double)0.0F, (double)1.0F));
         var1.add(var0.clone().add((double)-2.0F, (double)0.0F, (double)-1.0F));
         var1.add(var0.clone().add((double)-1.0F, (double)0.0F, (double)-2.0F));
         var1.add(var0.clone().add((double)1.0F, (double)0.0F, (double)-2.0F));
         var1.add(var0.clone().add((double)2.0F, (double)0.0F, (double)-1.0F));
         var1.add(var0.clone().add((double)2.0F, (double)0.0F, (double)1.0F));
      }

      var1.addAll(getPlatForm(var0.add((double)0.0F, (double)1.0F, (double)0.0F)));
      return var1;
   }

   public static ArrayList<Location> getSurroundingLocations(Location var0) {
      ArrayList var1 = new ArrayList();
      var1.add(var0.clone().add((double)-1.0F, (double)0.0F, (double)0.0F));
      var1.add(var0.clone().add((double)0.0F, (double)0.0F, (double)1.0F));
      var1.add(var0.clone().add((double)0.0F, (double)0.0F, (double)-1.0F));
      var1.add(var0.clone().add((double)1.0F, (double)0.0F, (double)0.0F));
      return var1;
   }

   public static void Rollback(BlockState var0) {
      if (var0 instanceof Sign) {
         Sign var1 = (Sign)var0;
         Location var2 = var1.getLocation();
         var0.update(true);
         Sign var3 = (Sign)var2.getWorld().getBlockAt(var2).getState();

         for(int var4 = 0; var4 < 4; ++var4) {
            var3.setLine(var4, var1.getLines()[var4]);
         }

         var3.update(true);
      } else {
         var0.update(true);
      }

   }

   public static ItemStack getItemStack(String var0, boolean var1, boolean var2) {
      String[] var3 = var0.split(" : ");
      String var4 = var3[0].toUpperCase();
      ItemStackBuilder var5 = null;
      if (var4.contains(":")) {
         if (var4.split(":")[0].equals("QUALITYARMORY")) {
            ItemStack var6 = null;
            if (Kitbattle.getInstance().QualityArmoryEnabled) {
               String var7 = var3[0].split(":")[1];
               if (QualityArmory.getGunByName(var7) != null) {
                  var6 = QualityArmory.getGunByName(var7).getItemStack();
               } else if (QualityArmory.getAmmoByName(var7) != null) {
                  var6 = QualityArmory.getAmmoByName(var7).getItemStack();
               } else if (QualityArmory.getArmorByName(var7) != null) {
                  var6 = QualityArmory.getArmorByName(var7).getItemStack();
               } else if (QualityArmory.getCustomItemAsItemStack(var7) != null) {
                  var6 = QualityArmory.getCustomItemAsItemStack(var7);
               }
            }

            if (var6 == null) {
               return null;
            }

            var5 = new ItemStackBuilder(var6);
         } else if (var4.split(":")[0].equals("CRACKSHOT")) {
            ItemStack var8 = null;
            if (Kitbattle.getInstance().crackShotAPI != null) {
               String var13 = var3[0].split(":")[1];
               var8 = Kitbattle.getInstance().crackShotAPI.generateWeapon(var13);
            }

            if (var8 == null) {
               return null;
            }

            var5 = new ItemStackBuilder(var8);
         } else if (var4.split(":")[0].equals("ITEMSADDER")) {
            if (Kitbattle.getInstance().ItemsAdderEnabled) {
               String var9 = var3[0].split(":")[1];
               CustomStack var14 = CustomStack.getInstance(var9);
               if (var14 == null) {
                  return null;
               }

               var5 = new ItemStackBuilder(var14.getItemStack());
            }
         } else if (var4.split(":")[0].equals("ITEMEDIT")) {
            ItemStack var10 = null;
            if (Kitbattle.getInstance().itemEdit != null) {
               String var15 = var3[0].split(":")[1];
               var10 = Kitbattle.getInstance().itemEdit.getItem(var15);
            }

            if (var10 == null) {
               return null;
            }

            var5 = new ItemStackBuilder(var10);
         }
      }

      if (var5 == null) {
         Optional var11 = XMaterial.matchXMaterial(var4);
         if (!var11.isPresent() && var4.contains(":")) {
            var11 = XMaterial.matchXMaterial(var4.split(":")[0]);
         }

         var5 = new ItemStackBuilder(((XMaterial)var11.get()).parseItem());
         if (var3[0].contains(":")) {
            String var16 = var4.split(":")[0];
            if ((var16.contains("POTION") || var16.equals("TIPPED_ARROW")) && var4.split(":").length == 4) {
               var5.setPotionEffect(PotionType.valueOf(var4.split(":")[1]), Boolean.parseBoolean(var3[0].split(":")[2]), Boolean.parseBoolean(var3[0].split(":")[3]));
            } else {
               var5.setDurability(Integer.parseInt(var4.split(":")[1]));
            }
         }
      }

      if (var1) {
         var5.setAmount(Integer.parseInt(var3[1]));
      }

      if (var2) {
         for(int var12 = var1 ? 2 : 1; var12 < var3.length; ++var12) {
            String var17 = var3[var12].split(":")[0].toLowerCase();
            if (var17.equals("enchant")) {
               var5.addEnchantment(var3[var12].split(":")[1], Integer.parseInt(var3[var12].split(":")[2]));
            } else if (var17.equals("name")) {
               var5.setName(colorize(var3[var12].split(":")[1]));
            } else if (var17.equals("lore")) {
               var5.addLore(colorize(var3[var12].split(":")[1]));
            } else if (var17.equals("dye")) {
               var5.setColor(getColor(var3[var12].split(":")[1]));
            } else if (var17.equals("modeldata")) {
               var5.setCustomModelData(Integer.valueOf(var3[var12].split(":")[1]));
            } else if (var17.equals("tag") && var3[var12].split(":")[1].equalsIgnoreCase("unbreakable")) {
               var5.tagUnbreakable();
            }
         }
      }

      return var5.build();
   }

   public static String itemStackToString(ItemStack var0) {
      String var1 = var0 != null ? var0.getType().name() : "";
      if (!var1.isEmpty()) {
         if (var0.getType().getMaxDurability() - var0.getDurability() != var0.getType().getMaxDurability()) {
            var1 = var1 + ":" + var0.getDurability();
         }

         var1 = var1 + " : " + var0.getAmount();
         if (var0.getItemMeta().getDisplayName() != null && !var0.getItemMeta().getDisplayName().isEmpty()) {
            var1 = var1 + " : name:" + var0.getItemMeta().getDisplayName();
         }

         if (var0.getItemMeta().getLore() != null && !var0.getItemMeta().getLore().isEmpty()) {
            for(String var3 : var0.getItemMeta().getLore()) {
               var1 = var1 + " : lore:" + var3;
            }
         }

         if (var0.getEnchantments() != null && !var0.getEnchantments().isEmpty()) {
            for(Enchantment var6 : var0.getEnchantments().keySet()) {
               var1 = var1 + " : enchant:" + var6.getName().toUpperCase() + ":" + var0.getEnchantments().get(var6);
            }
         }

         if (var0.getType().name().contains("LEATHER_")) {
            LeatherArmorMeta var5 = (LeatherArmorMeta)var0.getItemMeta();
            if (var5.getColor() != null) {
               var1 = var1 + " : dye:" + getColorName(var5.getColor());
            }
         }
      }

      return var1;
   }

   public static Color getColor(String var0) {
      if (var0.equalsIgnoreCase("AQUA")) {
         return Color.AQUA;
      } else if (var0.equalsIgnoreCase("BLUE")) {
         return Color.BLUE;
      } else if (var0.equalsIgnoreCase("FUCHSIA")) {
         return Color.FUCHSIA;
      } else if (var0.equalsIgnoreCase("GRAY")) {
         return Color.GRAY;
      } else if (var0.equalsIgnoreCase("GREEN")) {
         return Color.GREEN;
      } else if (var0.equalsIgnoreCase("LIME")) {
         return Color.LIME;
      } else if (var0.equalsIgnoreCase("MAROON")) {
         return Color.MAROON;
      } else if (var0.equalsIgnoreCase("NAVY")) {
         return Color.NAVY;
      } else if (var0.equalsIgnoreCase("OLIVE")) {
         return Color.OLIVE;
      } else if (var0.equalsIgnoreCase("ORANGE")) {
         return Color.ORANGE;
      } else if (var0.equalsIgnoreCase("PURPLE")) {
         return Color.PURPLE;
      } else if (var0.equalsIgnoreCase("RED")) {
         return Color.RED;
      } else if (var0.equalsIgnoreCase("SILVER")) {
         return Color.SILVER;
      } else if (var0.equalsIgnoreCase("TEAL")) {
         return Color.TEAL;
      } else if (var0.equalsIgnoreCase("WHITE")) {
         return Color.WHITE;
      } else {
         return var0.equalsIgnoreCase("YELLOW") ? Color.YELLOW : Color.BLACK;
      }
   }

   public static String getColorName(Color var0) {
      if (var0.equals(Color.AQUA)) {
         return "AQUA";
      } else if (var0.equals(Color.BLUE)) {
         return "BLUE";
      } else if (var0.equals(Color.FUCHSIA)) {
         return "FUCHSIA";
      } else if (var0.equals(Color.GRAY)) {
         return "GRAY";
      } else if (var0.equals(Color.GREEN)) {
         return "GREEN";
      } else if (var0.equals(Color.LIME)) {
         return "LIME";
      } else if (var0.equals(Color.MAROON)) {
         return "MAROON";
      } else if (var0.equals(Color.NAVY)) {
         return "NAVY";
      } else if (var0.equals(Color.OLIVE)) {
         return "OLIVE";
      } else if (var0.equals(Color.ORANGE)) {
         return "ORANGE";
      } else if (var0.equals(Color.PURPLE)) {
         return "PURPLE";
      } else if (var0.equals(Color.RED)) {
         return "RED";
      } else if (var0.equals(Color.SILVER)) {
         return "SILVER";
      } else if (var0.equals(Color.TEAL)) {
         return "TEAL";
      } else if (var0.equals(Color.WHITE)) {
         return "WHITE";
      } else {
         return var0.equals(Color.YELLOW) ? "YELLOW" : "BLACK";
      }
   }

   public static List<Player> getPlayers(Collection<UUID> var0) {
      ArrayList var1 = new ArrayList();

      for(UUID var3 : var0) {
         if (Bukkit.getPlayer(var3) != null) {
            var1.add(Bukkit.getPlayer(var3));
         }
      }

      return var1;
   }

   public static List<Player> getOnlinePlayers() {
      ArrayList var0 = Lists.newArrayList();

      for(World var2 : Bukkit.getWorlds()) {
         var0.addAll(var2.getPlayers());
      }

      return Collections.unmodifiableList(var0);
   }

   public static String getStringFromLocation(Location var0, boolean var1) {
      return var0.getWorld().getName() + ", " + ((double)var0.getBlockX() + (var1 ? (double)0.5F : (double)0.0F)) + ", " + (var0.getBlockY() + (var1 ? 1 : 0)) + ", " + ((double)var0.getBlockZ() + (var1 ? (double)0.5F : (double)0.0F)) + ", " + var0.getYaw() + ", " + var0.getPitch();
   }

   public static Location getLocationFromString(String var0) {
      String[] var1 = var0.split(", ");
      World var2 = Bukkit.getWorld(var1[0]);
      double var3 = Double.valueOf(var1[1]);
      double var5 = Double.valueOf(var1[2]);
      double var7 = Double.valueOf(var1[3]);
      float var9 = Float.valueOf(var1[4]);
      float var10 = Float.valueOf(var1[5]);
      return new Location(var2, var3, var5, var7, var9, var10);
   }

   public static String getReadableLocationString(Location var0, boolean var1) {
      return "" + ChatColor.GREEN + ((double)var0.getBlockX() + (var1 ? (double)0.5F : (double)0.0F)) + ChatColor.GRAY + ", " + ChatColor.GREEN + (var0.getBlockY() + (var1 ? 1 : 0)) + ChatColor.GRAY + ", " + ChatColor.GREEN + ((double)var0.getBlockZ() + (var1 ? (double)0.5F : (double)0.0F));
   }

   public static boolean compareItem(ItemStack var0, ItemStack var1) {
      if (var0 != null && var1 != null) {
         if (!var0.getType().equals(var1.getType())) {
            return false;
         } else if ((!var0.getItemMeta().hasDisplayName() || var1.getItemMeta().hasDisplayName()) && (var0.getItemMeta().hasDisplayName() || !var1.getItemMeta().hasDisplayName()) && (!var0.getItemMeta().hasDisplayName() || !var1.getItemMeta().hasDisplayName() || var0.getItemMeta().getDisplayName().equals(var1.getItemMeta().getDisplayName()))) {
            return (!var0.getItemMeta().hasLore() || var1.getItemMeta().hasLore()) && (var0.getItemMeta().hasLore() || !var1.getItemMeta().hasLore()) && (!var0.getItemMeta().hasLore() || !var1.getItemMeta().hasLore() || var0.getItemMeta().getLore().equals(var1.getItemMeta().getLore()));
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static int getInventorySize(int var0) {
      return var0 < 10 ? 9 : (var0 < 19 ? 18 : (var0 < 28 ? 27 : (var0 < 37 ? 36 : (var0 < 46 ? 45 : 54))));
   }

   public static boolean checkNumbers(String... var0) {
      try {
         for(String var4 : var0) {
            Integer.parseInt(var4);
         }

         return true;
      } catch (NumberFormatException var5) {
         return false;
      }
   }

   public static ChatColor getRandomColor() {
      return goodColors[random.nextInt(goodColors.length)];
   }

   public static String colorize(String var0) {
      for(Matcher var1 = HEX_PATTERN.matcher(var0); var1.find(); var1 = HEX_PATTERN.matcher(var0)) {
         String var2 = var0.substring(var1.start(), var1.end());
         String var3 = var2.replace("&#", "x");
         char[] var4 = var3.toCharArray();
         StringBuilder var5 = new StringBuilder();

         for(char var9 : var4) {
            var5.append("&").append(var9);
         }

         var0 = var0.replace(var2, var5.toString());
      }

      return ChatColor.translateAlternateColorCodes('&', var0);
   }

   static {
      goodColors = new ChatColor[]{ChatColor.DARK_AQUA, ChatColor.GOLD, ChatColor.GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW};
      HEX_PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}");
   }
}
