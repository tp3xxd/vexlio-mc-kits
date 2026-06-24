package me.wazup.kitbattle.commands.user;

import me.wazup.kitbattle.Kit;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.Messages;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.commands.SubCommand;
import me.wazup.kitbattle.events.PlayerSelectKitEvent;
import me.wazup.kitbattle.managers.PlayerDataManager;
import me.wazup.kitbattle.managers.TitleManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SelectKitCommand extends SubCommand {
   public SelectKitCommand() {
      super((String)null, false, "<Kit>");
   }

   public boolean execute(Kitbattle var1, Messages var2, CommandSender var3, String[] var4) {
      if (var4.length == 1) {
         return false;
      } else {
         Player var5 = (Player)var3;
         if (var1.players.contains(var5.getUniqueId())) {
            PlayerData var6 = PlayerDataManager.get(var5);
            if (var6.getKit() == null) {
               Kit var7 = (Kit)var1.Kits.get(var4[1].toLowerCase());
               if (var7 != null) {
                  if (var6.kitsInventory.getAllContents().contains(var7.getLogo())) {
                     if (!var7.isEnabled()) {
                        var5.sendMessage((String)var2.messages.get("Kit-Disabled"));
                        return true;
                     }

                     if (var7.requirePermission && !var5.hasPermission(var7.permission)) {
                        var5.sendMessage((String)var2.messages.get("No-Permission-For-Kit"));
                        return true;
                     }

                     if (var6.hasKitSelectionCooldown(var5, var7.getName())) {
                        return true;
                     }

                     var6.setKitSelectionCooldown(var7.getName(), var7.selectionCooldown);
                     var5.closeInventory();
                     var6.setKit(var5, var7);
                     var7.giveItems(var5);
                     if (!TitleManager.getInstance().sendTitle(var5, ((String)var2.messages.get("Player-Select-Kit")).replace("%kit%", var7.getName()))) {
                        var5.sendMessage(((String)var2.messages.get("Player-Select-Kit")).replace("%kit%", var7.getName()));
                     }

                     Bukkit.getPluginManager().callEvent(new PlayerSelectKitEvent(var5, var7));
                  } else {
                     var5.sendMessage((String)var2.messages.get("Kit-Select-Deny"));
                  }
               } else {
                  var5.sendMessage((String)var2.messages.get("Unknown-Kit"));
               }
            } else {
               var5.sendMessage((String)var2.messages.get("Already-Selected-Kit"));
            }
         } else {
            var5.sendMessage((String)var2.messages.get("Not-In-A-Game"));
         }

         return true;
      }
   }
}
