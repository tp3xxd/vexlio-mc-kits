package me.wazup.kitbattle.commands.user;

import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.Messages;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.commands.SubCommand;
import me.wazup.kitbattle.managers.PlayerDataManager;
import me.wazup.kitbattle.managers.SoundsManager;
import me.wazup.kitbattle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BountyCommand extends SubCommand {
   public BountyCommand() {
      super("kitbattle.bounty", false, "<Player> <Amount>");
   }

   public boolean execute(Kitbattle var1, Messages var2, CommandSender var3, String[] var4) {
      if (var4.length >= 3 && Utils.checkNumbers(var4[2])) {
         Player var5 = Bukkit.getPlayer(var4[1]);
         if (var5 == null) {
            var3.sendMessage((String)var2.messages.get("Player-Not-Found"));
            return true;
         } else {
            Player var6 = (Player)var3;
            PlayerData var7 = PlayerDataManager.get(var6);
            if (var7.hasCooldown(var6, "BOUNTY_SET")) {
               return true;
            } else {
               int var8 = Integer.parseInt(var4[2]);
               if (var8 < var1.config.MinimumBounty) {
                  var6.sendMessage(((String)var2.messages.get("Bounty-Deny")).replace("%bounty%", String.valueOf(var1.config.MinimumBounty)));
                  return true;
               } else if (var7.getCoins(var6) < var8) {
                  var6.sendMessage((String)var2.messages.get("Not-Enough-Coins"));
                  return true;
               } else {
                  var7.removeCoins(var6, var8);
                  var7.setCooldown(var6, "BOUNTY_SET", var1.config.BountySetCooldown, false);
                  PlayerData var9 = PlayerDataManager.get(var5);
                  var9.bounties.put(var6.getName(), var9.bounties.containsKey(var6.getName()) ? (Integer)var9.bounties.get(var6.getName()) + var8 : var8);
                  String var10 = ((String)var2.messages.get("Bounty-Set")).replace("%target%", var5.getName()).replace("%bounty%", String.valueOf(var8)).replace("%total_bounty%", String.valueOf(var9.getTotalBounty()));

                  for(Player var12 : Utils.getPlayers(var1.players)) {
                     var12.sendMessage(var10);
                     var12.playSound(var12.getLocation(), SoundsManager.NOTE_PLING, 1.0F, 1.0F);
                  }

                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }
}
