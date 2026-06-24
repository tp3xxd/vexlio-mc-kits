package eu.milujukockoholky.vexliokits.commands;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import org.bukkit.command.CommandSender;

public abstract class SubCommand {
   final String permission;
   final boolean allowConsole;
   final String arguments;
   final String[] argumentsExplaination;

   public SubCommand(String var1, boolean var2, String var3, String... var4) {
      this.permission = var1;
      this.allowConsole = var2;
      this.arguments = var3;
      this.argumentsExplaination = var4;
   }

   public abstract boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4);
}
