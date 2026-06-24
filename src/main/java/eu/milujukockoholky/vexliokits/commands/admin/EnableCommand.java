package eu.milujukockoholky.vexliokits.commands.admin;

import eu.milujukockoholky.vexliokits.BungeeMode;
import eu.milujukockoholky.vexliokits.ChallengesManager;
import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Map;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.TournamentManager;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class EnableCommand extends SubCommand {
   public EnableCommand() {
      super("VexlioKits.enable", false, "<Map>");
   }

   public boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4) {
      if (var4.length == 1) {
         return false;
      } else {
         String var5 = var4[1].toLowerCase();
         if (!var1.playingMaps.containsKey(var5) && !var1.tournamentMaps.containsKey(var5) && !var1.challengeMaps.containsKey(var5)) {
            var3.sendMessage((String)var2.messages.get("Unknown-Map"));
            return true;
         } else {
            Map var6 = var1.playingMaps.containsKey(var5) ? (Map)var1.playingMaps.get(var5) : (var1.tournamentMaps.containsKey(var5) ? (Map)var1.tournamentMaps.get(var5) : (Map)var1.challengeMaps.get(var5));
            if (var6.enabled) {
               var3.sendMessage(var2.prefix + "The map is already enabled!");
               return true;
            } else {
               FileConfiguration var7 = var1.fileManager.getConfig("maps.yml");
               var7.set("Maps." + var6.name + ".Enabled", true);
               var1.fileManager.saveConfig("maps.yml");
               var6.enabled = true;
               if (var1.playingMaps.containsKey(var5)) {
                  if (var1.config.bungeeMode) {
                     if (var1.bungeeMode != null) {
                        var1.bungeeMode.updateMap();
                     } else {
                        var1.bungeeMode = new BungeeMode(var1);
                     }
                  }
               } else if (var1.tournamentMaps.containsKey(var5)) {
                  if (var1.tournamentsManager == null && var6.isAvailable()) {
                     var1.tournamentsManager = new TournamentManager(var1);
                  } else if (var1.challengeMaps.containsKey(var5) && var1.challengesManager == null && var6.isAvailable()) {
                     var1.challengesManager = new ChallengesManager(var1);
                  }
               }

               var3.sendMessage(var2.prefix + "You have enabled the map!");
               return true;
            }
         }
      }
   }
}
