package eu.milujukockoholky.vexliokits.commands.user;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.PlayingMap;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand extends SubCommand {
   public JoinCommand() {
      super("VexlioKits.join", false, "<Map>");
   }

   public boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4) {
      Player var5 = (Player)var3;
      if (!var1.config.bungeeMode && !var1.isInTournament(var5) && !var1.isInChallenge(var5)) {
         if (var4.length == 1) {
            return false;
         } else {
            String var6 = var4[1].toLowerCase();
            if (!var1.playingMaps.containsKey(var6)) {
               var5.sendMessage((String)var2.messages.get("Unknown-Map"));
               return true;
            } else {
               PlayingMap var7 = (PlayingMap)var1.playingMaps.get(var6);
               if (!var7.enabled) {
                  var5.sendMessage((String)var2.messages.get("Map-Disabled"));
                  return true;
               } else {
                  Location var8 = var7.getSpawnpoint();
                  if (var8 == null) {
                     var5.sendMessage((String)var2.messages.get("No-Available-Spawnpoint"));
                     return true;
                  } else {
                     if (var1.players.contains(var5.getUniqueId())) {
                        PlayerData var9 = PlayerDataManager.get(var5);
                        if (var9.getMap() != null && var9.getMap().name.equals(var7.name)) {
                           var5.sendMessage((String)var2.messages.get("Player-Already-In-Map"));
                           return true;
                        }

                        if (var9.hasCooldown(var5, "Join-Another-Map")) {
                           return true;
                        }

                        var9.setCooldown(var5, "Join-Another-Map", 10, false);
                        var1.resetPlayerToMap(var5, var7, true);
                     } else {
                        var1.join(var5, var7, 0);
                     }

                     var5.sendMessage(((String)var2.messages.get("Player-Join-Map")).replace("%map%", var7.name));
                     return true;
                  }
               }
            }
         }
      } else {
         var5.sendMessage((String)var2.messages.get("Command-Disabled"));
         return true;
      }
   }
}
