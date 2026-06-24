package eu.milujukockoholky.vexliokits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import eu.milujukockoholky.vexliokits.utils.ItemStackBuilder;
import eu.milujukockoholky.vexliokits.utils.Utils;
import eu.milujukockoholky.vexliokits.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChallengesManager {
   public List<UUID> players;
   public HashMap<Integer, List<UUID>> normal_queues;
   public HashMap<Integer, List<UUID>> ranked_queues;
   private final VexlioKits plugin;

   public ChallengesManager(VexlioKits var1) {
      this.plugin = var1;
      this.normal_queues = new HashMap();
      this.ranked_queues = new HashMap();

      for(ChallengeMap var3 : var1.challengeMaps.values()) {
         if (!this.normal_queues.containsKey(var3.playersPerTeam)) {
            this.normal_queues.put(var3.playersPerTeam, new ArrayList());
            this.ranked_queues.put(var3.playersPerTeam, new ArrayList());
         }
      }

      this.players = new ArrayList();
   }

   public void add(Player var1, int var2, boolean var3) {
      if (this.normal_queues.containsKey(var2)) {
         if (var3) {
            if (((List)this.ranked_queues.get(var2)).contains(var1.getUniqueId())) {
               return;
            }

            ((List)this.ranked_queues.get(var2)).add(var1.getUniqueId());
         } else {
            if (((List)this.normal_queues.get(var2)).contains(var1.getUniqueId())) {
               return;
            }

            ((List)this.normal_queues.get(var2)).add(var1.getUniqueId());
         }

         this.checkQueue(var2, var3);
      }
   }

   public void remove(Player var1, int var2) {
      if (this.normal_queues.containsKey(var2)) {
         ((List)this.normal_queues.get(var2)).remove(var1.getUniqueId());
         ((List)this.ranked_queues.get(var2)).remove(var1.getUniqueId());
      }
   }

   public boolean isInQueue(Player var1, int var2) {
      if (!this.normal_queues.containsKey(var2)) {
         return false;
      } else {
         return ((List)this.normal_queues.get(var2)).contains(var1.getUniqueId()) || ((List)this.ranked_queues.get(var2)).contains(var1.getUniqueId());
      }
   }

   public void removeFromQueues(Player var1) {
      for(int var3 : this.normal_queues.keySet()) {
         ((List)this.normal_queues.get(var3)).remove(var1.getUniqueId());
      }

      for(int var5 : this.ranked_queues.keySet()) {
         ((List)this.ranked_queues.get(var5)).remove(var1.getUniqueId());
      }

   }

   public void checkQueue(int var1, boolean var2) {
      List var3 = var2 ? (List)this.ranked_queues.get(var1) : (List)this.normal_queues.get(var1);
      if (var3.size() >= var1 * 2) {
         ArrayList var4 = new ArrayList();

         for(ChallengeMap var6 : this.plugin.challengeMaps.values()) {
            if (var6.playersPerTeam == var1 && var6.isAvailable()) {
               var4.add(var6);
            }
         }

         if (var4.isEmpty()) {
            return;
         }

         ((ChallengeMap)var4.get(Utils.random.nextInt(var4.size()))).start(Utils.getPlayers(var3.subList(0, var1 * 2)), var2);
      }

   }

   public void addDuelers(Player var1, Player var2) {
      List var3 = (List)this.normal_queues.get(1);
      if (var3.size() % 2 == 0) {
         var3.add(var1.getUniqueId());
         var3.add(var2.getUniqueId());
      } else {
         var3.add(var3.size() - 1, var1.getUniqueId());
         var3.add(var3.size() - 1, var2.getUniqueId());
      }

      this.normal_queues.put(1, var3);
      this.checkQueue(1, false);
   }

   public boolean hasQueue(int var1) {
      return this.normal_queues.containsKey(var1);
   }

   public void openMenu(Player var1) {
      Inventory var2 = Bukkit.createInventory(var1, 9, (String)this.plugin.msgs.inventories.get("Queue"));

      for(int var4 : this.normal_queues.keySet()) {
         ItemStack var5 = ((List)this.normal_queues.get(var4)).contains(var1.getUniqueId()) ? XMaterial.LIME_DYE.parseItem() : (((List)this.ranked_queues.get(var4)).contains(var1.getUniqueId()) ? XMaterial.PINK_DYE.parseItem() : XMaterial.GRAY_DYE.parseItem());
         var2.addItem(new ItemStack[]{(new ItemStackBuilder(var5)).setName(ChatColor.AQUA + "" + var4 + "v" + var4).addLore(ChatColor.GREEN + "Left-click for unranked queue").addLore(ChatColor.LIGHT_PURPLE + "Right-click for ranked queue").build()});
      }

      var1.openInventory(var2);
   }
}
