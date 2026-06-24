package eu.milujukockoholky.vexliokits.commands.admin;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand {
   public ReloadCommand() {
      super("VexlioKits.reload", true, (String)null);
   }

   public boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4) {
      long var5 = System.currentTimeMillis();
      var1.setupAll();
      var3.sendMessage(var2.prefix + "The plugin has been reloaded! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var5) + "ms");
      return true;
   }
}
