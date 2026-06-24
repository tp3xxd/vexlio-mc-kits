package eu.milujukockoholky.vexliokits.commands.user;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LeaveCommand extends SubCommand {
   public LeaveCommand() {
      super((String)null, false, (String)null);
   }

   public boolean execute(final VexlioKits var1, final Messages var2, CommandSender var3, String[] var4) {
      final Player var5 = (Player)var3;
      if (!var1.config.bungeeMode && !var1.isInTournament(var5) && !var1.isInChallenge(var5)) {
         if (var1.players.contains(var5.getUniqueId())) {
            if (var1.config.LeaveCommandTimer > 0 && !var5.hasPermission("VexlioKits.leave.bypass")) {
               PlayerData var6 = PlayerDataManager.get(var5);
               if (var6.hasCooldown(var5, "LEAVE_COMMAND")) {
                  return true;
               }

               var6.setCooldown(var5, "LEAVE_COMMAND", var1.config.LeaveCommandTimer, false);
               final Location var7 = var5.getLocation().getBlock().getLocation();
               (new BukkitRunnable() {
                  int seconds;

                  {
                     this.seconds = var1.config.LeaveCommandTimer;
                  }

                  public void run() {
                     if (!var1.players.contains(var5.getUniqueId())) {
                        this.cancel();
                     } else if (!var5.getLocation().getBlock().getLocation().equals(var7)) {
                        var5.sendMessage((String)var2.messages.get("Player-Move"));
                        this.cancel();
                     } else if (this.seconds == 0) {
                        var1.leave(var5);
                        this.cancel();
                     } else {
                        var5.sendMessage(((String)var2.messages.get("Player-Movement-Disabled")).replace("%seconds%", String.valueOf(this.seconds)));
                     }

                     --this.seconds;
                  }
               }).runTaskTimer(var1, 0L, 20L);
            } else {
               var1.leave(var5);
            }
         } else {
            var5.sendMessage((String)var2.messages.get("Not-In-A-Game"));
         }

         return true;
      } else {
         var5.sendMessage((String)var2.messages.get("Command-Disabled"));
         return true;
      }
   }
}
