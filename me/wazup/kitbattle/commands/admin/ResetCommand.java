package me.wazup.kitbattle.commands.admin;

import java.io.File;
import java.sql.SQLException;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.Messages;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.commands.SubCommand;
import me.wazup.kitbattle.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ResetCommand extends SubCommand {
   public ResetCommand() {
      super("kitbattle.reset", true, "<Player>");
   }

   public boolean execute(final Kitbattle var1, final Messages var2, final CommandSender var3, String[] var4) {
      if (var4.length == 1) {
         return false;
      } else {
         Player var5 = Bukkit.getPlayer(var4[1]);
         if (var5 != null) {
            PlayerData var6 = PlayerDataManager.get(var5);
            var6.joined = true;
            var6.resetPlayer(var5);
            if (var1.players.contains(var5.getUniqueId())) {
               var6.createScoreboard(var5);
               if (var6.getKit() == null) {
                  var5.getInventory().clear();
                  var1.giveDefaultItems(var5);
               }
            }

            var3.sendMessage(var2.prefix + "The player " + ChatColor.AQUA + var4[1] + ChatColor.GRAY + " data has been " + ChatColor.GREEN + "successfully " + ChatColor.GRAY + "cleared!");
         } else {
            final long var9 = System.currentTimeMillis();
            final String[] var8 = new String[2];
            var1.fileManager.executeDatabaseUpdate(var3, var4[1], new BukkitRunnable() {
               public void run() {
                  if (var8[1] == null) {
                     try {
                        var1.mysql.getConnection().prepareStatement("delete from " + var1.config.tableprefix + " WHERE player_name= '" + var8[0] + "';").executeUpdate();
                        long var1x = System.currentTimeMillis() - var9;
                        var3.sendMessage(var2.prefix + "The player " + ChatColor.YELLOW + var8[0] + ChatColor.GOLD + " data has been erased! took " + ChatColor.LIGHT_PURPLE + var1x + "ms " + ChatColor.GOLD + "(" + ChatColor.AQUA + var1x / 1000L + "s" + ChatColor.GOLD + ") to erase the player data");
                     } catch (SQLException var4) {
                        var4.printStackTrace();
                     }
                  } else {
                     File var5 = new File(var1.getDataFolder() + "/players/", var8[0]);
                     var5.delete();
                     long var2x = System.currentTimeMillis() - var9;
                     var3.sendMessage(var2.prefix + "The player " + ChatColor.YELLOW + var8[1] + ChatColor.GOLD + " data has been erased! took " + ChatColor.LIGHT_PURPLE + var2x + "ms " + ChatColor.GOLD + "(" + ChatColor.AQUA + var2x / 1000L + "s" + ChatColor.GOLD + ") to erase the player data");
                  }

               }
            }, var8);
         }

         return true;
      }
   }
}
