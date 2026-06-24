package eu.milujukockoholky.vexliokits.commands.user;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
import eu.milujukockoholky.vexliokits.managers.SoundsManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleTournamentCommand extends SubCommand {
   public ToggleTournamentCommand() {
      super((String)null, false, (String)null);
   }

   public boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4) {
      Player var5 = (Player)var3;
      if (!var1.players.contains(var5.getUniqueId())) {
         var5.sendMessage((String)var2.messages.get("Not-In-A-Game"));
         return true;
      } else if (var1.tournamentsManager == null) {
         var5.sendMessage((String)var2.messages.get("Command-Disabled"));
         return true;
      } else {
         PlayerData var6 = PlayerDataManager.get(var5);
         if (var6.hasCooldown(var5, "Tournament")) {
            return true;
         } else {
            var6.setCooldown(var5, "Tournament", 2, false);
            if (!var1.tournamentsManager.isQueueing(var5) && !var1.tournamentsManager.contains(var5)) {
               var1.tournamentsManager.add(var5);
               var5.playSound(var5.getLocation(), SoundsManager.CLICK, 1.0F, 1.0F);
               var5.sendMessage((String)var2.messages.get("Tournament-Queue-Join"));
            } else {
               var1.tournamentsManager.remove(var5, false);
               var5.playSound(var5.getLocation(), SoundsManager.CLICK, 1.0F, 1.0F);
               var5.sendMessage((String)var2.messages.get("Tournament-Queue-Leave"));
            }

            return true;
         }
      }
   }
}
