package me.wazup.kitbattle.commands.admin;

import me.wazup.kitbattle.ChallengeMap;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.Map;
import me.wazup.kitbattle.Messages;
import me.wazup.kitbattle.PlayingMap;
import me.wazup.kitbattle.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class DisableCommand extends SubCommand {
   public DisableCommand() {
      super("kitbattle.disable", true, "<Map>");
   }

   public boolean execute(Kitbattle var1, Messages var2, CommandSender var3, String[] var4) {
      if (var4.length == 1) {
         return false;
      } else {
         String var5 = var4[1].toLowerCase();
         if (!var1.playingMaps.containsKey(var5) && !var1.tournamentMaps.containsKey(var5) && !var1.challengeMaps.containsKey(var5)) {
            var3.sendMessage((String)var2.messages.get("Unknown-Map"));
            return true;
         } else {
            Map var6 = var1.playingMaps.containsKey(var5) ? (Map)var1.playingMaps.get(var5) : (var1.tournamentMaps.containsKey(var5) ? (Map)var1.tournamentMaps.get(var5) : (Map)var1.challengeMaps.get(var5));
            if (!var6.enabled) {
               var3.sendMessage(var2.prefix + "The map is already disabled");
               return true;
            } else {
               FileConfiguration var7 = var1.fileManager.getConfig("maps.yml");
               var7.set("Maps." + var6.name + ".Enabled", false);
               var1.fileManager.saveConfig("maps.yml");
               var6.enabled = false;
               if (var1.playingMaps.containsKey(var5)) {
                  ((PlayingMap)var6).removePlayers();
               } else if (var1.tournamentMaps.containsKey(var5) && var1.tournamentsManager != null && var1.tournamentsManager.isRunning() && var1.tournamentsManager.map.name.equals(var6.name)) {
                  var1.tournamentsManager.stop();
               } else if (var1.challengeMaps.containsKey(var5)) {
                  if (((ChallengeMap)var6).isRunning()) {
                     ((ChallengeMap)var6).stop();
                  }

                  int var8 = 0;

                  for(ChallengeMap var10 : var1.challengeMaps.values()) {
                     if (var10.isAvailable()) {
                        ++var8;
                     }
                  }

                  if (var8 == 0) {
                     var1.challengesManager = null;
                  } else {
                     int var12 = 0;

                     for(ChallengeMap var11 : var1.challengeMaps.values()) {
                        if (var11.playersPerTeam == ((ChallengeMap)var6).playersPerTeam && var11.isAvailable()) {
                           ++var12;
                        }
                     }

                     if (var12 == 0) {
                        var1.challengesManager.normal_queues.remove(((ChallengeMap)var6).playersPerTeam);
                        var1.challengesManager.ranked_queues.remove(((ChallengeMap)var6).playersPerTeam);
                     }
                  }
               }

               var3.sendMessage(var2.prefix + "You have updated the map state!");
               return true;
            }
         }
      }
   }
}
