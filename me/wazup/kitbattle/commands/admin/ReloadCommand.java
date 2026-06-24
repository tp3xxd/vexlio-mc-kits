package me.wazup.kitbattle.commands.admin;

import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.Messages;
import me.wazup.kitbattle.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand {
   public ReloadCommand() {
      super("kitbattle.reload", true, (String)null);
   }

   public boolean execute(Kitbattle var1, Messages var2, CommandSender var3, String[] var4) {
      long var5 = System.currentTimeMillis();
      var1.setupAll();
      var3.sendMessage(var2.prefix + "The plugin has been reloaded! took " + ChatColor.LIGHT_PURPLE + (System.currentTimeMillis() - var5) + "ms");
      return true;
   }
}
