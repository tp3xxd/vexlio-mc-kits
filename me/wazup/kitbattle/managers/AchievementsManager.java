package me.wazup.kitbattle.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.utils.ItemStackBuilder;
import me.wazup.kitbattle.utils.SmartInventory;
import me.wazup.kitbattle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AchievementsManager {
   public final boolean enabled;
   private final HashMap<AchievementType, ArrayList<Achievement>> achievements = new HashMap();
   private final Kitbattle plugin;

   public AchievementsManager(Kitbattle var1) {
      this.plugin = var1;
      FileConfiguration var2 = var1.fileManager.getConfig("achievements.yml");
      this.enabled = var2.getBoolean("Enabled");

      for(AchievementType var6 : AchievementsManager.AchievementType.values()) {
         String var7 = "Achievements." + var6.name().toLowerCase();
         ArrayList var8 = new ArrayList();
         if (!var2.getConfigurationSection(var7).getKeys(false).isEmpty()) {
            for(String var10 : var2.getConfigurationSection(var7).getKeys(false)) {
               var7 = "Achievements." + var6.name().toLowerCase() + "." + var10 + ".";
               var8.add(new Achievement(Integer.valueOf(var10), var2.getString(var7 + "description"), var2.getString(var7 + "prize-description"), var2.getString(var7 + "executed-command")));
            }
         }

         this.achievements.put(var6, var8);
      }

      List var11 = this.getAchievements();
      int var12 = 0;

      for(int var13 = 0; (double)var13 < Math.ceil(Double.valueOf((double)var11.size()) / (double)SmartInventory.smartSlots.length); ++var13) {
         for(int var18 : SmartInventory.smartSlots) {
            if (var12 >= var11.size()) {
               break;
            }

            ((Achievement)var11.get(var12)).id = var13;
            ((Achievement)var11.get(var12)).slot = var18;
            ++var12;
         }
      }

   }

   public SmartInventory getAchievements(PlayerData var1) {
      SmartInventory var2 = new SmartInventory(this.plugin, (String)this.plugin.msgs.inventories.get("Achievements-Inventory"));

      for(int var3 = 0; (double)var3 < Math.ceil(Double.valueOf((double)this.getSize()) / (double)SmartInventory.smartSlots.length); ++var3) {
         var2.addInventory(ChatColor.BLUE + "List #" + (var3 + 1));
         var2.setItem(var3, 49, this.plugin.back_itemstack);
      }

      int[] var12 = new int[]{var1.getKills(), var1.getProjectileHits(), var1.getTournamentWins(), var1.getChallengeWins(), var1.getAbilitiesUsed(), var1.getSoupsEaten(), var1.getKillstreaksEarned()};
      if (var12.length != AchievementsManager.AchievementType.values().length) {
         return null;
      } else {
         for(AchievementType var7 : AchievementsManager.AchievementType.values()) {
            int var8 = var12[var7.ordinal()];

            for(Achievement var10 : (ArrayList)this.achievements.get(var7)) {
               ItemStack var11 = (new ItemStackBuilder(var8 >= var10.score ? this.plugin.config.achievementUnlocked : this.plugin.config.achievementLocked)).setName(this.plugin.config.achievementDescription.replace("%description%", var10.description)).addLore(var8 >= var10.score ? this.plugin.config.achievementUnlockedLore : this.plugin.config.achievementLockedLore, " ", this.plugin.config.achievementPrize.replace("%prizeDescription%", var10.prizeDescription)).build();
               var2.setItem(var10.id, var10.slot, var11);
            }
         }

         return var2;
      }
   }

   public void checkPlayer(Player var1, AchievementType var2, int var3) {
      if (this.enabled) {
         for(Achievement var5 : (ArrayList)this.achievements.get(var2)) {
            if (var3 == var5.score) {
               var5.send(var1);
               break;
            }
         }

      }
   }

   public List<Achievement> getAchievements() {
      ArrayList var1 = new ArrayList();

      for(AchievementType var5 : AchievementsManager.AchievementType.values()) {
         var1.addAll((Collection)this.achievements.get(var5));
      }

      return var1;
   }

   public int getSize() {
      return this.getAchievements().size();
   }

   public static enum AchievementType {
      KILLS("Get %x% kills!", 5, new int[]{10, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000}),
      PROJECTILES_HIT("Hit %x% projectile!", 10, new int[]{5, 20, 50, 100, 200, 300, 400, 500}),
      TOURNAMENTS_WON("Win %x% tournament(s)!", 100, new int[]{1, 3, 5, 10, 20, 50, 100}),
      CHALLENGES_WON("Win %x% challenge(s)!", 100, new int[]{1, 5, 20, 50, 100, 150, 200, 250, 300}),
      ABILITIES_USED("Use an ability %x% times!", 5, new int[]{5, 50, 100, 200, 300, 400, 500, 750, 1000}),
      SOUPS_EATEN("Eat %x% soups!", 2, new int[]{10, 100, 500, 1000, 1500, 2000, 3000, 4000, 5000}),
      KILLSTREAKS_EARNED("Earn %x% killstreaks!", 10, new int[]{5, 10, 20, 50, 100, 200});

      public String defaultDescription;
      public int prizeMultiplier;
      public int[] levels;

      private AchievementType(String var3, int var4, int... var5) {
         this.defaultDescription = var3;
         this.prizeMultiplier = var4;
         this.levels = var5;
      }

      // $FF: synthetic method
      private static AchievementType[] $values() {
         return new AchievementType[]{KILLS, PROJECTILES_HIT, TOURNAMENTS_WON, CHALLENGES_WON, ABILITIES_USED, SOUPS_EATEN, KILLSTREAKS_EARNED};
      }
   }

   public class Achievement {
      int score;
      String description;
      String prizeDescription;
      String executedCommand;
      int id;
      int slot;

      public Achievement(int var2, String var3, String var4, String var5) {
         this.score = var2;
         this.description = var3;
         this.prizeDescription = var4;
         this.executedCommand = var5;
      }

      public void send(Player var1) {
         ChatColor var2 = Utils.getRandomColor();

         for(String var4 : Kitbattle.getInstance().msgs.achievementMessages) {
            var1.sendMessage(var4.replace("%randomcolor%", var2.toString()).replace("%description%", this.description).replace("%prizeDescription%", this.prizeDescription));
         }

         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.executedCommand.replace("%player%", var1.getName()));
         AchievementsManager.this.plugin.listen.spawnFirework(var1.getLocation());
         var1.playSound(var1.getLocation(), SoundsManager.NOTE_PLING, 1.0F, 1.0F);
         (new ItemStackBuilder(PlayerDataManager.get(var1).achievements.getItem(this.id, this.slot))).setType(Material.DIAMOND_BLOCK).replaceLore(ChatColor.RED + "Locked", ChatColor.GREEN + "Unlocked").build();
      }
   }
}
