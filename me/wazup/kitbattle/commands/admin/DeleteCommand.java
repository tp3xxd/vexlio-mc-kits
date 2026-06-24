package me.wazup.kitbattle.commands.admin;

import me.wazup.kitbattle.ChallengeMap;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.Map;
import me.wazup.kitbattle.Messages;
import me.wazup.kitbattle.PlayingMap;
import me.wazup.kitbattle.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DeleteCommand extends SubCommand {
   public DeleteCommand() {
      super("kitbattle.delete", true, "<Map>");
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
            var1.fileManager.getConfig("maps.yml").set("Maps." + var6.name, (Object)null);
            var1.fileManager.saveConfig("maps.yml");
            if (var1.playingMaps.containsKey(var5)) {
               ((PlayingMap)var1.playingMaps.get(var5)).removePlayers();
               var1.playingMaps.remove(var5);
            } else if (var1.tournamentMaps.containsKey(var5)) {
               var1.tournamentMaps.remove(var5);
               if (var1.tournamentsManager != null && var1.tournamentsManager.map.name.toLowerCase().equals(var5)) {
                  if (var1.tournamentMaps.isEmpty()) {
                     var1.tournamentsManager.clearQueue();
                  }

                  var1.tournamentsManager.stop();
               }
            } else {
               ChallengeMap var7 = (ChallengeMap)var1.challengeMaps.get(var5);
               var1.challengeMaps.remove(var5);
               if (var7.isRunning()) {
                  var7.stop();
               }

               if (var1.challengeMaps.isEmpty()) {
                  var1.challengesManager = null;
               }
            }

            var3.sendMessage(var2.prefix + "The map " + ChatColor.AQUA + var6.name + ChatColor.GRAY + " was deleted successfully!");
            return true;
         }
      }
   }
}
