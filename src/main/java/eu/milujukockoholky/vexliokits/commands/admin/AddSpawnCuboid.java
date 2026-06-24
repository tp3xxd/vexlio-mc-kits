package eu.milujukockoholky.vexliokits.commands.admin;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.Messages;
import eu.milujukockoholky.vexliokits.PlayingMap;
import eu.milujukockoholky.vexliokits.commands.SubCommand;
import eu.milujukockoholky.vexliokits.utils.Cuboid;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AddSpawnCuboid extends SubCommand {
   public AddSpawnCuboid() {
      super("VexlioKits.addspawncuboid", false, "<Map>");
   }

   public boolean execute(VexlioKits var1, Messages var2, CommandSender var3, String[] var4) {
      Player var5 = (Player)var3;
      if (var4.length == 1) {
         return false;
      } else if (!var1.playingMaps.containsKey(var4[1].toLowerCase())) {
         var5.sendMessage((String)var2.messages.get("Unknown-Map"));
         return true;
      } else if (var1.selectionMode.containsKey(var5.getUniqueId()) && ((Location[])var1.selectionMode.get(var5.getUniqueId()))[0] != null && ((Location[])var1.selectionMode.get(var5.getUniqueId()))[1] != null) {
         PlayingMap var6 = (PlayingMap)var1.playingMaps.get(var4[1].toLowerCase());
         Cuboid var7 = new Cuboid(((Location[])var1.selectionMode.get(var5.getUniqueId()))[0], ((Location[])var1.selectionMode.get(var5.getUniqueId()))[1]);
         FileConfiguration var8 = var1.fileManager.getConfig("maps.yml");
         int var9 = var8.getConfigurationSection("Maps." + var6.name + ".Spawn-Cuboids") != null && !var8.getConfigurationSection("Maps." + var6.name + ".Spawn-Cuboids").getKeys(false).isEmpty() ? var8.getConfigurationSection("Maps." + var6.name + ".Spawn-Cuboids").getKeys(false).size() + 1 : 1;
         var8.set("Maps." + var6.name + ".Spawn-Cuboids." + var9, var7.toString());
         var1.fileManager.saveConfig("maps.yml");
         var6.spawnCuboids.add(var7);
         var5.sendMessage(var2.prefix + "You have added a new cuboid with the id of " + ChatColor.AQUA + "#" + var9 + ChatColor.GRAY + " to the map " + ChatColor.AQUA + var6.name + "!");
         return true;
      } else {
         var5.sendMessage(var2.prefix + "You haven't selected the 2 corners yet!");
         return true;
      }
   }
}
