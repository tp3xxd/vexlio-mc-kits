package me.wazup.kitbattle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import me.wazup.kitbattle.managers.PlayerDataManager;
import me.wazup.kitbattle.managers.SoundsManager;
import me.wazup.kitbattle.utils.ItemStackBuilder;
import me.wazup.kitbattle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BungeeMode {
   HashMap<UUID, String> playerVotes;
   public Inventory voteInventory;
   private final Kitbattle plugin;
   private PlayingMap map;
   private int mapIndex;
   private BukkitTask shuffler;
   private BukkitTask countdown;

   public BungeeMode(Kitbattle var1) {
      this.plugin = var1;
      this.mapIndex = 0;
      this.updateMap();
   }

   public void updateMap() {
      List var1 = this.getAvailableMaps();
      if (!var1.isEmpty()) {
         if (this.mapIndex >= var1.size()) {
            this.mapIndex = 0;
         }

         this.map = (PlayingMap)var1.get(this.mapIndex);
         if (var1.size() > 1) {
            if (this.shuffler == null) {
               this.startShuffler();
            }

            this.voteInventory = Bukkit.createInventory((InventoryHolder)null, Utils.getInventorySize(var1.size() - 1), (String)this.plugin.msgs.inventories.get("Map-Vote"));
            this.playerVotes = new HashMap();

            for(PlayingMap var3 : var1) {
               if (!this.map.name.equals(var3.name)) {
                  this.voteInventory.addItem(new ItemStack[]{(new ItemStackBuilder(Material.NAME_TAG)).setName(ChatColor.GREEN + var3.name).addLore(ChatColor.GOLD + "" + ChatColor.BOLD + "Votes: " + ChatColor.YELLOW + 0).build()});
               }
            }
         } else {
            this.cancelTasks();
         }
      } else {
         this.kickAll();
      }

   }

   public void startShuffler() {
      this.shuffler = (new BukkitRunnable() {
         public void run() {
            BungeeMode.this.cancelTasks();
            BungeeMode.this.countdown = (new BukkitRunnable() {
               int seconds;

               {
                  this.seconds = BungeeMode.this.plugin.config.highestTimeShownBeforeShuffle;
               }

               public void run() {
                  if (BungeeMode.this.plugin.config.timeShownBeforeShuffle.contains(this.seconds)) {
                     String var1 = ((String)BungeeMode.this.plugin.msgs.messages.get("Map-Switch-Count-Down")).replace("%time%", String.valueOf(this.seconds));

                     for(Player var3 : Utils.getOnlinePlayers()) {
                        var3.sendMessage(var1);
                        var3.playSound(var3.getLocation(), SoundsManager.CLICK, 1.0F, 1.0F);
                     }
                  }

                  --this.seconds;
                  if (this.seconds == 0) {
                     BungeeMode.this.cancelTasks();
                     BungeeMode.this.changeMap();
                  }

               }
            }).runTaskTimer(BungeeMode.this.plugin, 0L, 20L);
         }
      }).runTaskLater(this.plugin, (long)((this.plugin.config.shuffleEveryMinutes * 60 - this.plugin.config.highestTimeShownBeforeShuffle) * 20));
   }

   public void changeMap() {
      ++this.mapIndex;
      if (this.playerVotes != null && !this.playerVotes.isEmpty()) {
         int var1 = 0;
         HashMap var2 = new HashMap();
         List var3 = this.getAvailableMaps();

         for(PlayingMap var5 : var3) {
            int var6 = this.getVotes(var5.name.toLowerCase());
            var2.put(var5.name.toLowerCase(), var6);
            if (var6 > var1) {
               var1 = var6;
            }
         }

         ArrayList var9 = new ArrayList();

         for(String var12 : var2.keySet()) {
            if (((Integer)var2.get(var12)).equals(var1)) {
               var9.add(var12);
            }
         }

         String var11 = (String)var9.get(Utils.random.nextInt(var9.size()));

         for(int var13 = 0; var13 < var3.size(); ++var13) {
            if (((PlayingMap)var3.get(var13)).name.toLowerCase().equals(var11)) {
               this.mapIndex = var13;
               break;
            }
         }
      }

      this.updateMap();
      if (this.map != null) {
         for(Player var8 : Utils.getOnlinePlayers()) {
            if (!this.plugin.isInTournament(var8) && !this.plugin.isInChallenge(var8)) {
               this.plugin.resetPlayerToMap(var8, this.map, true);
            } else {
               PlayerDataManager.get(var8).setMap(var8, this.map);
            }
         }
      } else {
         this.kickAll();
      }

   }

   public void vote(Player var1, ItemStack var2) {
      String var3 = ChatColor.stripColor(var2.getItemMeta().getDisplayName()).toLowerCase();
      String var4 = "";
      if (this.plugin.playingMaps.containsKey(var3) && ((PlayingMap)this.plugin.playingMaps.get(var3)).isAvailable()) {
         if (this.playerVotes.containsKey(var1.getUniqueId())) {
            if (((String)this.playerVotes.get(var1.getUniqueId())).equals(var3)) {
               return;
            }

            var4 = (String)this.playerVotes.get(var1.getUniqueId());
         }

         this.playerVotes.put(var1.getUniqueId(), var3);
         this.updateVotes(var3);
         if (!var4.isEmpty()) {
            this.updateVotes(var4);
         }

      }
   }

   public void updateVotes(String var1) {
      for(ItemStack var5 : this.voteInventory.getContents()) {
         if (var5 != null && ChatColor.stripColor(var5.getItemMeta().getDisplayName().toLowerCase()).equals(var1)) {
            (new ItemStackBuilder(var5)).replaceLore("Votes", ChatColor.GOLD + "" + ChatColor.BOLD + "Votes: " + ChatColor.YELLOW + this.getVotes(var1)).build();
            break;
         }
      }

   }

   public int getVotes(String var1) {
      int var2 = 0;

      for(String var4 : this.playerVotes.values()) {
         if (var4.equals(var1)) {
            ++var2;
         }
      }

      return var2;
   }

   public void kickAll() {
      for(Player var2 : Utils.getOnlinePlayers()) {
         var2.kickPlayer((String)this.plugin.msgs.messages.get("No-Available-Maps"));
      }

      this.plugin.bungeeMode = null;
      this.cancelTasks();
   }

   public PlayingMap getMap() {
      return this.map;
   }

   public boolean isShufflerRunning() {
      return this.shuffler != null || this.countdown != null;
   }

   public List<PlayingMap> getAvailableMaps() {
      ArrayList var1 = new ArrayList();

      for(PlayingMap var3 : this.plugin.playingMaps.values()) {
         if (var3.isAvailable()) {
            var1.add(var3);
         }
      }

      return var1;
   }

   private void cancelTasks() {
      if (this.shuffler != null) {
         this.shuffler.cancel();
         this.shuffler = null;
      }

      if (this.countdown != null) {
         this.countdown.cancel();
         this.countdown = null;
      }

   }
}
