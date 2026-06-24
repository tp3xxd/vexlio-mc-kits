package eu.milujukockoholky.vexliokits.commands.user;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand extends SubCommand {
   public StatsCommand() {
      super((String)null, true, "<Player>");
   }

   public boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4) {
      Player var5;
      if (var4.length == 1) {
         if (!(var3 instanceof Player)) {
            return false;
         }

         var5 = (Player)var3;
      } else {
         var5 = Bukkit.getPlayer(var4[1]);
      }

      if (var5 == null) {
         var3.sendMessage((String)var2.messages.get("Player-Not-Found"));
         return true;
      } else {
         PlayerDataManager.get(var5).sendStats(var3, var5);
         return true;
      }
   }
}
