package me.wazup.kitbattle.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DecentHologramsHologram extends Hologram {
   eu.decentsoftware.holograms.api.holograms.Hologram hologram;

   public DecentHologramsHologram(String var1, Location var2, List<String> var3) {
      this.hologram = DHAPI.createHologram(var1, var2, false, var3);
   }

   public void delete() {
      this.hologram.delete();
   }

   void teleport(Location var1) {
      DHAPI.moveHologram(this.hologram, var1);
   }

   void showToOnePlayer(Player var1) {
      this.hologram.setDefaultVisibleState(false);
      this.hologram.setShowPlayer(var1);
   }

   void updateLines(List<String> var1) {
      DHAPI.setHologramLines(this.hologram, var1);
   }
}
