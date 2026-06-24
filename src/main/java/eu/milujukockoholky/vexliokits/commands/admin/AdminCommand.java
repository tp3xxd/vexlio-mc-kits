package eu.milujukockoholky.vexliokits.commands.admin;

import eu.milujukockoholky.vexliokits.Config;
import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AdminCommand extends SubCommand {
   public AdminCommand() {
      super("VexlioKits.admin", true, (String)null);
   }

   public boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4) {
      var3.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + " ----------" + ChatColor.AQUA + " VexlioKits Admin " + ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + "----------");
      var3.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/vexliokits " + ChatColor.GREEN + "Wand" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Gives you the selection wand!");
      var3.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/vexliokits " + ChatColor.GREEN + "Create" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Create a new map!");
      var3.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/vexliokits " + ChatColor.GREEN + "Delete" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Delete a map!");
      var3.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/vexliokits " + ChatColor.GREEN + "Addspawn / Removespawn" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Add/Remove spawnpoints");
      var3.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/vexliokits " + ChatColor.GREEN + "Enable / Disable" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Enables/Disables maps");
      var3.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/vexliokits " + ChatColor.GREEN + "Addspawncuboid / Removespawncuboid" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Add/Remove a spawn region");
      var3.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/vexliokits " + ChatColor.GREEN + "Reset" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Resets a player data");
      var3.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/vexliokits " + ChatColor.GREEN + "Coins / Exp" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Modify a player coins or exp");
      var3.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/vexliokits " + ChatColor.GREEN + "Kit" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Modify the kits");
      var3.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/vexliokits " + ChatColor.GREEN + "KitUnlocker" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Give a player Kitunlockers");
      var3.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/vexliokits " + ChatColor.GREEN + "Holograms" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Manages holographic features");
      if (!Config.getInstance().AllowBuilding) {
         var3.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/vexliokits " + ChatColor.GREEN + "Editmode" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Allows you to break/place blocks while ingame");
      }

      var3.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/vexliokits " + ChatColor.GREEN + "Reload" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Reload the config files");
      var3.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + " ---------------------------------");
      return true;
   }
}
