package me.wazup.kitbattle.commands.user;

import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.Messages;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.commands.SubCommand;
import me.wazup.kitbattle.managers.PlayerDataManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiCommand extends SubCommand {
   public GuiCommand() {
      super((String)null, false, "Shop/Kits/Profile/Trails/Challenges");
   }

   public boolean execute(Kitbattle var1, Messages var2, CommandSender var3, String[] var4) {
      Player var5 = (Player)var3;
      if (var4.length == 1) {
         return false;
      } else {
         if (var1.players.contains(var5.getUniqueId())) {
            PlayerData var6 = PlayerDataManager.get(var5);
            String var7 = var4[1].toLowerCase();
            if (var7.equals("shop")) {
               var1.shop.open(var5);
            } else if (var7.equals("kits")) {
               if (var6.getKit() == null) {
                  var6.kitsInventory.open(var5);
               } else {
                  var5.sendMessage((String)var2.messages.get("Command-Disabled"));
               }
            } else if (var7.equals("profile")) {
               var5.openInventory(var1.profileInventory);
            } else if (var7.equals("trails")) {
               var5.openInventory(var1.trailsInventory);
            } else {
               if (!var7.equals("challenges")) {
                  return false;
               }

               if (!var1.isInChallenge(var5) && !var1.isInTournament(var5)) {
                  var1.challengesManager.openMenu(var5);
               } else {
                  var5.sendMessage((String)var2.messages.get("Command-Disabled"));
               }
            }
         } else {
            var5.sendMessage((String)var2.messages.get("Not-In-A-Game"));
         }

         return true;
      }
   }
}
