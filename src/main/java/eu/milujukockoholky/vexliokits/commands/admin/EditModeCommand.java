package eu.milujukockoholky.vexliokits.commands.admin;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditModeCommand extends SubCommand {
   public EditModeCommand() {
      super("VexlioKits.editmode", false, (String)null);
   }

   public boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4) {
      Player var5 = (Player)var3;
      if (var1.config.AllowBuilding) {
         var5.sendMessage((String)var2.messages.get("Command-Disabled"));
         return true;
      } else {
         if (var1.editmode.contains(var5.getUniqueId())) {
            var1.editmode.remove(var5.getUniqueId());
            var5.sendMessage(var2.prefix + "You can no longer build");
         } else {
            var1.editmode.add(var5.getUniqueId());
            var5.sendMessage(var2.prefix + "You can now build");
         }

         return true;
      }
   }
}
