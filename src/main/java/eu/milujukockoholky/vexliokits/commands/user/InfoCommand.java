package eu.milujukockoholky.vexliokits.commands.user;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class InfoCommand extends SubCommand {
   public InfoCommand() {
      super((String)null, true, (String)null);
   }

   public boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4) {
      var3.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + " ----------" + ChatColor.AQUA + " Information " + ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + "----------");
      var3.sendMessage(ChatColor.AQUA + "Version: " + ChatColor.GREEN + var1.getDescription().getVersion());
      var3.sendMessage(ChatColor.AQUA + "BungeeMode: " + ChatColor.GREEN + (var1.bungeeMode != null));
      var3.sendMessage(ChatColor.AQUA + "Tournaments: " + ChatColor.GREEN + (var1.tournamentsManager != null));
      var3.sendMessage(ChatColor.AQUA + "Vault: " + ChatColor.GREEN + (var1.econ != null));
      var3.sendMessage(ChatColor.AQUA + "UUID: " + ChatColor.GREEN + var1.config.UUID);
      var3.sendMessage(ChatColor.AQUA + "MySQL: " + ChatColor.GREEN + var1.config.useMySQL);
      var3.sendMessage(ChatColor.AQUA + "Scoreboard: " + ChatColor.GREEN + var1.config.ScoreboardEnabled);
      var3.sendMessage(ChatColor.AQUA + "Next leaderboard update: " + (var1.leaderboard_updater == null ? ChatColor.RED + "Task is off" : ChatColor.GREEN + String.valueOf((var1.leaderboard_updater_time - System.currentTimeMillis()) / 1000L) + ChatColor.AQUA + "s " + ChatColor.LIGHT_PURPLE + "(" + (var1.leaderboard_updater_time - System.currentTimeMillis()) / 60000L + "m)"));
      var3.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + " ---------------------------------");
      return true;
   }
}
