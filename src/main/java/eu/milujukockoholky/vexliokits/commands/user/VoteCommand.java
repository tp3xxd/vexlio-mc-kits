package eu.milujukockoholky.vexliokits.commands.user;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCommand extends SubCommand {
   public VoteCommand() {
      super((String)null, false, (String)null);
   }

   public boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4) {
      Player var5 = (Player)var3;
      if (var1.config.bungeeMode && var1.bungeeMode.isShufflerRunning()) {
         var5.openInventory(var1.bungeeMode.voteInventory);
         return true;
      } else {
         var5.sendMessage((String)var2.messages.get("Command-Disabled"));
         return true;
      }
   }
}
