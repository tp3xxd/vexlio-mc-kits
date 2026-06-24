package eu.milujukockoholky.vexliokits.commands.admin;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.PlayingMap;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class RemoveSpawnCuboid extends SubCommand {
   public RemoveSpawnCuboid() {
      super("VexlioKits.removespawncuboid", true, "<Map>");
   }

   public boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4) {
      if (var4.length == 1) {
         return false;
      } else if (!var1.playingMaps.containsKey(var4[1].toLowerCase())) {
         var3.sendMessage((String)var2.messages.get("Unknown-Map"));
         return true;
      } else {
         PlayingMap var5 = (PlayingMap)var1.playingMaps.get(var4[1].toLowerCase());
         FileConfiguration var6 = var1.fileManager.getConfig("maps.yml");
         if (!var5.spawnCuboids.isEmpty() && var6.getConfigurationSection("Maps." + var5.name + ".Spawn-Cuboids") != null && !var6.getConfigurationSection("Maps." + var5.name + ".Spawn-Cuboids").getKeys(false).isEmpty()) {
            int var7 = var5.spawnCuboids.size();
            var6.set("Maps." + var5.name + ".Spawn-Cuboids." + var7, (Object)null);
            var1.fileManager.saveConfig("maps.yml");
            var5.spawnCuboids.remove(var5.spawnCuboids.get(var7 - 1));
            var3.sendMessage(var2.prefix + "You have removed the last spawn cuboid that was added! " + ChatColor.AQUA + "(#" + var7 + ")");
            return true;
         } else {
            var3.sendMessage(var2.prefix + "The map does not have any spawn cuboids!");
            return true;
         }
      }
   }
}
