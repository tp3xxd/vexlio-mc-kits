package me.wazup.kitbattle.commands.user;

import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.Messages;
import me.wazup.kitbattle.commands.SubCommand;
import me.wazup.kitbattle.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand extends SubCommand {
   public StatsCommand() {
      super((String)null, true, "<Player>");
   }

   public boolean execute(Kitbattle var1, Messages var2, CommandSender var3, String[] var4) {
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
