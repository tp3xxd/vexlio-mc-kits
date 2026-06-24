package eu.milujukockoholky.vexliokits.commands.admin;

import java.util.ArrayList;
import java.util.HashMap;
import eu.milujukockoholky.vexliokits.ChallengeMap;
import eu.milujukockoholky.vexliokits.ChallengesManager;
import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.PlayingMap;
import eu.milujukockoholky.vexliokits.TournamentManager;
import eu.milujukockoholky.vexliokits.TournamentMap;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import eu.milujukockoholky.vexliokits.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CreateCommand extends SubCommand {
   public CreateCommand() {
      super("VexlioKits.create", false, "<Map> <Normal/Tournament/Challenge>");
   }

   public boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4) {
      if (var4.length >= 3 && (var4[2].equalsIgnoreCase("Normal") || var4[2].equalsIgnoreCase("Tournament") || var4[2].equalsIgnoreCase("Challenge"))) {
         Player var5 = (Player)var3;
         if (!var4[2].equalsIgnoreCase("Challenge") || var4.length >= 4 && var4[3].length() == 3 && var4[3].toLowerCase().charAt(1) == 'v' && Utils.checkNumbers(String.valueOf(var4[3].charAt(0)), String.valueOf(var4[3].charAt(2))) && Integer.valueOf(String.valueOf(var4[3].charAt(2))) == Integer.valueOf(String.valueOf(var4[3].charAt(0)))) {
            String var6 = var4[1].toLowerCase();
            if (!var1.playingMaps.containsKey(var6) && !var1.tournamentMaps.containsKey(var6) && !var1.challengeMaps.containsKey(var6)) {
               FileConfiguration var7 = var1.fileManager.getConfig("maps.yml");
               var7.set("Maps." + var4[1] + ".Type", var4[2]);
               if (var4[2].equalsIgnoreCase("Challenge")) {
                  var7.set("Maps." + var4[1] + ".Players-Per-Team", Integer.valueOf(String.valueOf(var4[3].charAt(2))));
               }

               var7.set("Maps." + var4[1] + ".Enabled", true);
               var1.fileManager.saveConfig("maps.yml");
               if (var4[2].equalsIgnoreCase("Normal")) {
                  var1.playingMaps.put(var6, new PlayingMap(var1, var4[1], new ArrayList(), new ArrayList(), true, new HashMap()));
                  if (var1.bungeeMode != null) {
                     var1.bungeeMode.updateMap();
                  }
               } else if (var4[2].equalsIgnoreCase("Tournament")) {
                  var1.tournamentMaps.put(var6, new TournamentMap(var1, var4[1], new ArrayList(), true));
                  if (var1.tournamentsManager == null) {
                     var1.tournamentsManager = new TournamentManager(var1);
                  }
               } else if (var4[2].equalsIgnoreCase("Challenge")) {
                  var1.challengeMaps.put(var6, new ChallengeMap(var1, var4[1], new ArrayList(), Integer.valueOf(String.valueOf(var4[3].charAt(2))), true));
                  if (var1.challengesManager == null) {
                     var1.challengesManager = new ChallengesManager(var1);
                  }
               }

               var5.sendMessage(var2.prefix + "The map " + ChatColor.AQUA + var4[1] + ChatColor.GRAY + " has been created successfully!");
               var5.performCommand("vexliokits addspawn " + var6);
               return true;
            } else {
               var5.sendMessage(var2.prefix + "There is already a map with that name!");
               return true;
            }
         } else {
            var5.sendMessage(var2.prefix + "Usage: /vexliokits " + ChatColor.GREEN + "Create" + ChatColor.GRAY + " <Map> Challenge 1v1/2v2/3v3/XvX");
            return true;
         }
      } else {
         return false;
      }
   }
}
