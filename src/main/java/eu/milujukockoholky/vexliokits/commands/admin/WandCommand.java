package eu.milujukockoholky.vexliokits.commands.admin;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WandCommand extends SubCommand {
   public WandCommand() {
      super("VexlioKits.wand", false, (String)null);
   }

   public boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4) {
      Player var5 = (Player)var3;
      if (!var1.selectionMode.containsKey(var5.getUniqueId())) {
         var5.getInventory().addItem(new ItemStack[]{var1.wand_itemstack});
         var1.selectionMode.put(var5.getUniqueId(), new Location[2]);
         var5.sendMessage(var2.prefix + "You have entered the selection mode!");
      } else {
         var5.getInventory().removeItem(new ItemStack[]{var1.wand_itemstack});
         var1.selectionMode.remove(var5.getUniqueId());
         var5.sendMessage(var2.prefix + "You have left the selection mode!");
      }

      return true;
   }
}
