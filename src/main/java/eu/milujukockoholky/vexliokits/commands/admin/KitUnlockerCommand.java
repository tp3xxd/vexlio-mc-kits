package eu.milujukockoholky.vexliokits.commands.admin;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
import eu.milujukockoholky.vexliokits.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitUnlockerCommand extends SubCommand {
   public KitUnlockerCommand() {
      super("VexlioKits.kitunlocker", true, "give <Player> <Amount>");
   }

   public boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4) {
      if (var4.length >= 4 && Utils.checkNumbers(var4[3]) && var4[1].equalsIgnoreCase("give")) {
         Player var5 = Bukkit.getPlayer(var4[2]);
         if (var5 == null) {
            var3.sendMessage(var2.prefix + "Couldn't find that player!");
            return true;
         } else {
            PlayerData var6 = PlayerDataManager.get(var5);
            var6.kitUnlockers += Integer.parseInt(var4[3]);
            var6.joined = true;
            if (var1.players.contains(var5.getUniqueId()) && var6.getKit() == null) {
               var5.getInventory().clear();
               var1.giveDefaultItems(var5);
            }

            var5.sendMessage(((String)var2.messages.get("Player-Receive-KitUnlocker")).replace("%amount%", var4[3]));
            var3.sendMessage(var2.prefix + ChatColor.GREEN + var5.getName() + ChatColor.GRAY + " has received " + ChatColor.LIGHT_PURPLE + var4[3] + ChatColor.GRAY + " Kitunlockers successfully");
            return true;
         }
      } else {
         return false;
      }
   }
}
