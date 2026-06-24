package eu.milujukockoholky.vexliokits.commands.user;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.PlayingMap;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SpectateCommand extends SubCommand {
   public SpectateCommand() {
      super("VexlioKits.spectate", false, (String)null);
   }

   public boolean execute(final VexlioKits var1, final Messages var2, CommandSender var3, String[] var4) {
      final Player var5 = (Player)var3;
      if (!var1.isInTournament(var5) && !var1.isInChallenge(var5)) {
         if (GameMode.values().length < 4) {
            var5.sendMessage((String)var2.messages.get("Spectator-Mode-Not-Supported"));
            return true;
         } else if (!var1.players.contains(var5.getUniqueId())) {
            var5.sendMessage((String)var2.messages.get("Not-In-A-Game"));
            return true;
         } else {
            final PlayerData var6 = PlayerDataManager.get(var5);
            final PlayingMap var7 = var6.getMap();
            if (!var1.spectating.contains(var5.getUniqueId())) {
               final int var8 = var5.hasPermission("VexlioKits.spectate.bypass") ? 0 : var1.config.spectateCountdownSeconds;
               final Location var9 = var5.getLocation().getBlock().getLocation();
               if (var6.hasCooldown(var5, "Spectate")) {
                  return true;
               }

               var6.setCooldown(var5, "Spectate", var8, false);
               (new BukkitRunnable() {
                  int seconds = var8;

                  public void run() {
                     if (Bukkit.getPlayer(var5.getName()) == null) {
                        this.cancel();
                     } else if (var5.getLocation().getBlock().getLocation().equals(var9) && var6.getMap().name.equals(var7.name)) {
                        if (this.seconds == 0) {
                           var1.spectating.add(var5.getUniqueId());
                           var5.setGameMode(GameMode.valueOf("SPECTATOR"));
                           var5.sendMessage((String)var2.messages.get("Spectator-Mode-Enable"));
                           this.cancel();
                        } else {
                           var5.sendMessage(((String)var2.messages.get("Movement-Not-Allowed")).replace("%seconds%", String.valueOf(this.seconds)));
                           --this.seconds;
                        }
                     } else {
                        var5.sendMessage((String)var2.messages.get("Movement-Occur"));
                        this.cancel();
                     }
                  }
               }).runTaskTimer(var1, 0L, 20L);
            } else {
               var1.spectating.remove(var5.getUniqueId());
               var5.setGameMode(GameMode.valueOf("SURVIVAL"));
               var5.teleport(var7.getSpawnpoint());
               var5.sendMessage((String)var2.messages.get("Spectator-Mode-Disable"));
            }

            return true;
         }
      } else {
         var5.sendMessage((String)var2.messages.get("Command-Disabled"));
         return true;
      }
   }
}
