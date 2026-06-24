package eu.milujukockoholky.vexliokits.commands.admin;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HologramsCommand extends SubCommand {
   public HologramsCommand() {
      super("VexlioKits.holograms", false, "Set/Remove Leaderboard");
   }

   public boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4) {
      Player var5 = (Player)var3;
      String var6;
      if (var4.length >= 3 && ((var6 = var4[1].toLowerCase()).equals("set") || var6.equals("remove")) && var4[2].toLowerCase().equals("leaderboard")) {
         if (var1.hologramsManager == null) {
            var3.sendMessage(var2.prefix + "HolographicDisplays doesn't seem to be loaded!");
            return true;
         } else {
            if (var6.equals("set")) {
               Location var8 = var5.getLocation();
               var8.add((double)0.0F, (double)3.0F, (double)0.0F);
               var1.hologramsManager.setLeaderboardLocation(var8, true);
               var1.getConfig().set("Holographic-Leaderboard.world", var8.getWorld().getName());
               var1.getConfig().set("Holographic-Leaderboard.x", var8.getBlockX());
               var1.getConfig().set("Holographic-Leaderboard.y", var8.getBlockY());
               var1.getConfig().set("Holographic-Leaderboard.z", var8.getBlockZ());
               var5.sendMessage(var2.prefix + "You have set the holographic leaderboard location!");
            } else {
               var1.hologramsManager.setLeaderboardLocation((Location)null, true);
               var1.getConfig().set("Holographic-Leaderboard", (Object)null);
               var5.sendMessage(var2.prefix + "You have removed the holographic leaderboard location!");
            }

            var1.saveConfig();
            return true;
         }
      } else {
         return false;
      }
   }
}
