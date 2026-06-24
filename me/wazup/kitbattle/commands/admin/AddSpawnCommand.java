package me.wazup.kitbattle.commands.admin;

import me.wazup.kitbattle.BungeeMode;
import me.wazup.kitbattle.ChallengesManager;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.Map;
import me.wazup.kitbattle.Messages;
import me.wazup.kitbattle.TournamentManager;
import me.wazup.kitbattle.commands.SubCommand;
import me.wazup.kitbattle.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AddSpawnCommand extends SubCommand {
   public AddSpawnCommand() {
      super("kitbattle.addspawn", false, "<Map>");
   }

   public boolean execute(Kitbattle var1, Messages var2, CommandSender var3, String[] var4) {
      Player var5 = (Player)var3;
      if (var4.length == 1) {
         return false;
      } else {
         String var6 = var4[1].toLowerCase();
         if (!var1.playingMaps.containsKey(var6) && !var1.tournamentMaps.containsKey(var6) && !var1.challengeMaps.containsKey(var6)) {
            var5.sendMessage((String)var2.messages.get("Unknown-Map"));
            return true;
         } else {
            Map var7 = var1.playingMaps.containsKey(var6) ? (Map)var1.playingMaps.get(var6) : (var1.tournamentMaps.containsKey(var6) ? (Map)var1.tournamentMaps.get(var6) : (Map)var1.challengeMaps.get(var6));
            FileConfiguration var8 = var1.fileManager.getConfig("maps.yml");
            int var9 = var8.getConfigurationSection("Maps." + var7.name + ".Spawnpoints") != null && !var8.getConfigurationSection("Maps." + var7.name + ".Spawnpoints").getKeys(false).isEmpty() ? var8.getConfigurationSection("Maps." + var7.name + ".Spawnpoints").getKeys(false).size() + 1 : 1;
            String var10 = Utils.getStringFromLocation(var5.getLocation(), true);
            var8.set("Maps." + var7.name + ".Spawnpoints." + var9, var10);
            var1.fileManager.saveConfig("maps.yml");
            var7.spawnpoints.add(Utils.getLocationFromString(var10));
            if (var1.playingMaps.containsKey(var6) && var1.config.bungeeMode && var7.enabled) {
               if (var1.bungeeMode == null) {
                  var1.bungeeMode = new BungeeMode(var1);
               } else if (var7.spawnpoints.size() == 1) {
                  var1.bungeeMode.updateMap();
               }
            } else if (var1.tournamentMaps.containsKey(var6) && var1.tournamentsManager == null && var7.isAvailable()) {
               var1.tournamentsManager = new TournamentManager(var1);
            } else if (var1.challengeMaps.containsKey(var6) && var1.challengesManager == null && var7.isAvailable()) {
               var1.challengesManager = new ChallengesManager(var1);
            }

            var5.sendMessage(var2.prefix + "You have added a new spawnpoint with the id of " + ChatColor.AQUA + "#" + var9 + ChatColor.GRAY + " to the map " + ChatColor.AQUA + var7.name + " at " + Utils.getReadableLocationString(var5.getLocation(), true));
            return true;
         }
      }
   }
}
