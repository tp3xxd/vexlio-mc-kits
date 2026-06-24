package me.wazup.kitbattle.holograms;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import java.util.List;
import me.wazup.kitbattle.Kitbattle;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HolographicDisplaysHologram extends Hologram {
   com.gmail.filoghost.holographicdisplays.api.Hologram hologram;

   public HolographicDisplaysHologram(Location var1, List<String> var2) {
      this.hologram = HologramsAPI.createHologram(Kitbattle.getInstance(), var1);

      for(String var4 : var2) {
         this.hologram.appendTextLine(var4);
      }

   }

   public void delete() {
      this.hologram.delete();
   }

   void teleport(Location var1) {
      this.hologram.teleport(var1);
   }

   void showToOnePlayer(Player var1) {
      this.hologram.getVisibilityManager().showTo(var1);
      this.hologram.getVisibilityManager().setVisibleByDefault(false);
   }

   void updateLines(List<String> var1) {
      Location var2 = this.hologram.getLocation();
      this.hologram.delete();
      this.hologram = HologramsAPI.createHologram(Kitbattle.getInstance(), var2);

      for(String var4 : var1) {
         this.hologram.appendTextLine(var4);
      }

   }
}
