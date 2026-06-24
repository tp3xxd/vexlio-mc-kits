package me.wazup.kitbattle.commands.user;

import me.wazup.kitbattle.ChallengeMap;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.Map;
import me.wazup.kitbattle.Messages;
import me.wazup.kitbattle.PlayingMap;
import me.wazup.kitbattle.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ListCommand extends SubCommand {
   public ListCommand() {
      super((String)null, true, (String)null);
   }

   public boolean execute(Kitbattle var1, Messages var2, CommandSender var3, String[] var4) {
      var3.sendMessage(ChatColor.AQUA + "" + ChatColor.STRIKETHROUGH + "--------------------------------------");
      String var5 = var1.playingMaps.isEmpty() ? ChatColor.RED + "None" : "";

      for(PlayingMap var7 : var1.playingMaps.values()) {
         var5 = var5 + (var5.isEmpty() ? "" : ", ") + (var7.isAvailable() ? ChatColor.GREEN + var7.name + " (" + var7.players.size() + ")" : ChatColor.RED + var7.name);
      }

      var3.sendMessage(var2.prefix + "Maps: " + var5);
      String var10 = var1.tournamentMaps.isEmpty() ? ChatColor.RED + "None" : "";

      for(Map var8 : var1.tournamentMaps.values()) {
         var10 = var10 + (var10.isEmpty() ? "" : ", ") + (var8.enabled ? (var1.tournamentsManager.map != null && var1.tournamentsManager.map.name.equals(var8.name) ? ChatColor.RED + var8.name + " (" + var1.tournamentsManager.getSize() + ")" : ChatColor.GREEN + var8.name) : ChatColor.RED + var8.name);
      }

      var3.sendMessage(var2.prefix + "Tournament maps: " + var10);
      String var12 = var1.challengeMaps.isEmpty() ? ChatColor.RED + "None" : "";

      for(ChallengeMap var9 : var1.challengeMaps.values()) {
         var12 = var12 + (var12.isEmpty() ? "" : ", ") + (var9.enabled ? (var9.isAvailable() ? ChatColor.GREEN + var9.name : ChatColor.RED + var9.name + " (" + var9.players.size() + ")") : ChatColor.RED + var9.name);
      }

      var3.sendMessage(var2.prefix + "Challenge maps: " + var12);
      var3.sendMessage(ChatColor.AQUA + "" + ChatColor.STRIKETHROUGH + "--------------------------------------");
      return true;
   }
}
