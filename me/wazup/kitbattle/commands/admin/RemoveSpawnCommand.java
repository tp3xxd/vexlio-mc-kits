package me.wazup.kitbattle.commands.admin;

import me.wazup.kitbattle.ChallengeMap;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.Map;
import me.wazup.kitbattle.Messages;
import me.wazup.kitbattle.PlayingMap;
import me.wazup.kitbattle.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class RemoveSpawnCommand extends SubCommand {
   public RemoveSpawnCommand() {
      super("kitbattle.removespawn", true, "<Map>");
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
            FileConfiguration var7 = var1.fileManager.getConfig("maps.yml");
            if (!var6.spawnpoints.isEmpty() && var7.getConfigurationSection("Maps." + var6.name + ".Spawnpoints") != null && !var7.getConfigurationSection("Maps." + var6.name + ".Spawnpoints").getKeys(false).isEmpty()) {
               int var8 = var6.spawnpoints.size();
               var7.set("Maps." + var6.name + ".Spawnpoints." + var8, (Object)null);
               var1.fileManager.saveConfig("maps.yml");
               var6.spawnpoints.remove(var6.spawnpoints.get(var8 - 1));
               if (var1.playingMaps.containsKey(var5) && var6.spawnpoints.isEmpty()) {
                  ((PlayingMap)var6).removePlayers();
               } else if (!var1.tournamentMaps.containsKey(var5) || !var6.spawnpoints.isEmpty() || var1.tournamentsManager == null || var1.tournamentsManager.isStarting() || var1.tournamentsManager.isRunning() && !var1.tournamentsManager.map.name.equals(var6.name)) {
                  if (var1.challengeMaps.containsKey(var5) && var1.challengesManager != null) {
                     ChallengeMap var9 = (ChallengeMap)var6;
                     if (!var9.isAvailable()) {
                        if (((ChallengeMap)var6).isRunning()) {
                           ((ChallengeMap)var6).stop();
                        }

                        int var10 = 0;

                        for(ChallengeMap var12 : var1.challengeMaps.values()) {
                           if (var12.isAvailable()) {
                              ++var10;
                           }
                        }

                        if (var10 == 0) {
                           var1.challengesManager = null;
                        } else {
                           int var14 = 0;

                           for(ChallengeMap var13 : var1.challengeMaps.values()) {
                              if (var13.playersPerTeam == ((ChallengeMap)var6).playersPerTeam && var9.isAvailable()) {
                                 ++var14;
                              }
                           }

                           if (var14 == 0) {
                              var1.challengesManager.normal_queues.remove(((ChallengeMap)var6).playersPerTeam);
                              var1.challengesManager.ranked_queues.remove(((ChallengeMap)var6).playersPerTeam);
                           }
                        }
                     }
                  }
               } else {
                  var1.tournamentsManager.stop();
               }

               var3.sendMessage(var2.prefix + "You have removed the last spawnpoint that was added! " + ChatColor.AQUA + "(#" + var8 + ")");
               return true;
            } else {
               var3.sendMessage(var2.prefix + "The map does not have any spawnpoints!");
               return true;
            }
         }
      }
   }
}
