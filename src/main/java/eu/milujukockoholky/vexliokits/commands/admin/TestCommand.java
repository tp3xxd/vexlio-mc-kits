package eu.milujukockoholky.vexliokits.commands.admin;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import org.bukkit.command.CommandSender;

public class TestCommand extends SubCommand {
   public TestCommand() {
      super("VexlioKits.wazup92", true, (String)null);
   }

   public boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4) {
      return true;
   }
}
