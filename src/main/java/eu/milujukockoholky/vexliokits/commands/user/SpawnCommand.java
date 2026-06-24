package eu.milujukockoholky.vexliokits.commands.user;

import eu.milujukockoholky.vexliokits.Kit;
import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.PlayingMap;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnCommand extends SubCommand {
   public SpawnCommand() {
      super("VexlioKits.spawn", false, (String)null);
   }

   public boolean execute(final VexlioKits var1, final Messages var2, CommandSender var3, String[] var4) {
      final Player var5 = (Player)var3;
      if (!var1.isInTournament(var5) && !var1.isInChallenge(var5)) {
         final PlayerData var6 = PlayerDataManager.get(var5);
         if (!var1.players.contains(var5.getUniqueId())) {
            var5.sendMessage((String)var2.messages.get("Not-In-A-Game"));
            return true;
         } else {
            final Location var7 = var5.getLocation().getBlock().getLocation();
            final PlayingMap var8 = var6.getMap();
            final int var9 = var5.hasPermission("VexlioKits.spawn.bypass") ? 0 : var1.config.spawnTeleportCountdownSeconds;
            if (var6.hasCooldown(var5, "Spawn")) {
               return true;
            } else {
               var6.setCooldown(var5, "Spawn", var9, false);
               var6.teleportTask = (new BukkitRunnable() {
                  int seconds = var9;

                  public void run() {
                     if (!var5.isOnline()) {
                        this.cancel();
                     } else if (var5.getLocation().getBlock().getLocation().equals(var7) && var6.getMap().name.equals(var8.name)) {
                        if (this.seconds != 0) {
                           var5.sendMessage(((String)var2.messages.get("Movement-Not-Allowed")).replace("%seconds%", String.valueOf(this.seconds)));
                           --this.seconds;
                        } else {
                           var5.teleport(var8.getSpawnpoint());
                           var5.closeInventory();
                           if (var6.getKit() != null) {
                              String var1x = var6.getKit().getName();
                              var6.setKit(var5, (Kit)null);
                              var5.getInventory().clear();
                              var5.getInventory().setArmorContents((ItemStack[])null);

                              for(PotionEffect var3 : var5.getActivePotionEffects()) {
                                 var5.removePotionEffect(var3.getType());
                              }

                              var5.setFireTicks(0);
                              var5.setHealth(var5.getMaxHealth());
                              if (var5.getVehicle() != null) {
                                 Entity var4 = var5.getVehicle();
                                 if (var1.toRemove.contains(var4)) {
                                    var1.toRemove.remove(var4);
                                    var4.remove();
                                 }
                              }

                              var6.damagers.clear();
                              var5.setAllowFlight(false);
                              var5.setFlying(false);
                              var1.giveDefaultItems(var5);
                              var5.sendMessage(((String)var2.messages.get("Player-Reset-Kit")).replace("%kit%", var1x));
                           }

                           this.cancel();
                           var6.teleportTask = null;
                        }
                     } else {
                        var5.sendMessage((String)var2.messages.get("Movement-Occur"));
                        var6.teleportTask = null;
                        this.cancel();
                     }
                  }
               }).runTaskTimer(var1, 0L, 20L);
               return true;
            }
         }
      } else {
         var5.sendMessage((String)var2.messages.get("Command-Disabled"));
         return true;
      }
   }
}
