package me.wazup.kitbattle.commands.admin;

import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.Messages;
import me.wazup.kitbattle.commands.SubCommand;
import org.bukkit.command.CommandSender;

public class TestCommand extends SubCommand {
   public TestCommand() {
      super("kitbattle.wazup92", true, (String)null);
   }

   public boolean execute(Kitbattle var1, Messages var2, CommandSender var3, String[] var4) {
      return true;
   }
}
