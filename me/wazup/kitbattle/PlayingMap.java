package me.wazup.kitbattle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import me.wazup.kitbattle.managers.PlayerDataManager;
import me.wazup.kitbattle.utils.Cuboid;
import me.wazup.kitbattle.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class PlayingMap extends Map {
   public List<Cuboid> spawnCuboids;
   public HashMap<Location, Integer> signs;
   public List<UUID> players;

   public PlayingMap(Kitbattle var1, String var2, List<Location> var3, List<Cuboid> var4, boolean var5, HashMap<Location, Integer> var6) {
      super(var1, var2, var3, var5);
      this.spawnCuboids = var4;
      this.players = new ArrayList();
      this.signs = var6;
   }

   public void removePlayers() {
      if (this.plugin.bungeeMode != null) {
         if (this.plugin.bungeeMode.getMap().name.equals(this.name)) {
            this.plugin.bungeeMode.changeMap();
         } else {
            this.plugin.bungeeMode.updateMap();
         }

      } else {
         int var1 = 0;

         for(PlayingMap var3 : this.plugin.playingMaps.values()) {
            if (var3.enabled && var3.spawnpoints.size() > 0) {
               ++var1;
            }
         }

         if (var1 > 0) {
            for(Player var7 : Utils.getPlayers(this.plugin.players)) {
               if (PlayerDataManager.get(var7).getMap().name.equals(this.name)) {
                  if (!this.plugin.isInTournament(var7) && !this.plugin.isInChallenge(var7)) {
                     var7.sendMessage((String)this.plugin.msgs.messages.get("Map-Deleted-Send-To-Another-Map"));
                     var7.performCommand("kb join " + this.plugin.playingMaps.values().iterator().next());
                  } else {
                     PlayerDataManager.get(var7).setMap(var7, (PlayingMap)this.plugin.playingMaps.values().iterator().next());
                  }
               }
            }
         } else {
            ArrayList var6 = new ArrayList();

            for(Player var4 : Utils.getPlayers(this.plugin.players)) {
               if (PlayerDataManager.get(var4).getMap().name.equals(this.name)) {
                  var4.sendMessage((String)this.plugin.msgs.messages.get("Map-Deleted-Kit"));
                  var6.add(var4);
               }
            }

            for(Player var10 : var6) {
               var10.performCommand("kb leave");
            }
         }

      }
   }

   public boolean isInSpawn(Player var1) {
      for(Cuboid var3 : this.spawnCuboids) {
         if (var3.contains(var1.getLocation())) {
            return true;
         }
      }

      return false;
   }

   public void updateSignPlayers() {
      for(Location var2 : this.signs.keySet()) {
         if (var2.getBlock().getState() instanceof Sign) {
            Sign var3 = (Sign)var2.getBlock().getState();
            var3.setLine(3, ChatColor.AQUA + String.valueOf(this.players.size()) + ChatColor.YELLOW + " Players");
            var3.update(true);
         }
      }

   }
}
