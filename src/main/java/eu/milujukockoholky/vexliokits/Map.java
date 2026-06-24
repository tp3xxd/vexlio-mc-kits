package eu.milujukockoholky.vexliokits;

import java.util.List;
import eu.milujukockoholky.vexliokits.utils.Utils;
import org.bukkit.Location;

public abstract class Map {
   public String name;
   VexlioKits plugin;
   public List<Location> spawnpoints;
   public boolean enabled;

   public Map(VexlioKits var1, String var2, List<Location> var3, boolean var4) {
      this.plugin = var1;
      this.name = var2;
      this.spawnpoints = var3;
      this.enabled = var4;
   }

   public Location getSpawnpoint() {
      return this.spawnpoints != null && !this.spawnpoints.isEmpty() ? (Location)this.spawnpoints.get(Utils.random.nextInt(this.spawnpoints.size())) : null;
   }

   public boolean isAvailable() {
      return this.enabled && !this.spawnpoints.isEmpty();
   }
}
