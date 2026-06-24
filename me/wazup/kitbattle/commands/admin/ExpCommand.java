package me.wazup.kitbattle.commands.admin;

import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.Messages;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.commands.SubCommand;
import me.wazup.kitbattle.managers.PlayerDataManager;
import me.wazup.kitbattle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpCommand extends SubCommand {
   public ExpCommand() {
      super("kitbattle.exp", true, "Add/Set/Remove <Player> <Amount>");
   }

   public boolean execute(Kitbattle var1, Messages var2, CommandSender var3, String[] var4) {
      if (var4.length >= 4 && (var4[1].equalsIgnoreCase("add") || var4[1].equalsIgnoreCase("set") || var4[1].equalsIgnoreCase("remove")) && Utils.checkNumbers(var4[3])) {
         Player var5 = Bukkit.getPlayer(var4[2]);
         String var6 = var4[1].toLowerCase();
         int var7 = Integer.parseInt(var4[3]);
         if (var5 != null) {
            PlayerData var8 = PlayerDataManager.get(var5);
            if (var6.equals("add")) {
               var8.addExp(var5, var7);
            } else if (var6.equals("set")) {
               var8.setExp(var5, var7);
            } else if (var6.equals("remove")) {
               var8.setExp(var5, var8.getExp() - var7);
            }

            int var9 = var8.getExp();
            var3.sendMessage(var2.prefix + "You have modified the player " + ChatColor.AQUA + var5.getName() + ChatColor.GRAY + " exp to (" + ChatColor.GREEN + var9 + ChatColor.GRAY + ")");
            var5.sendMessage(((String)var2.messages.get("Player-Stat-Modification-Through-Command")).replace("%amount%", String.valueOf(var9)).replace("%stat%", "exp"));
            if (var1.players.contains(var5.getUniqueId())) {
               var8.createScoreboard(var5);
            }
         } else {
            var3.sendMessage(var2.prefix + "The ability to perform this command on offline players has been temporarily disabled");
         }

         return true;
      } else {
         return false;
      }
   }
}
