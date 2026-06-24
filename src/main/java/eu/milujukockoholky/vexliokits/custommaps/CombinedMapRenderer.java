package eu.milujukockoholky.vexliokits.custommaps;

import java.awt.Image;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import eu.milujukockoholky.vexliokits.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CombinedMapRenderer extends MapRenderer {
   List<String> texts;
   Image image;

   public CombinedMapRenderer(JavaPlugin var1, List<String> var2, final String var3, boolean var4) {
      this.texts = var2;
      if (var3 != null && !var3.isEmpty()) {
         if (var4) {
            (new BukkitRunnable() {
               public void run() {
                  CombinedMapRenderer.this.loadImage(var3);
               }
            }).runTaskAsynchronously(var1);
         } else {
            this.loadImage(var3);
         }

      }
   }

   private void loadImage(String var1) {
      try {
         int var2 = this.texts.isEmpty() ? 128 : 64;
         this.image = ImageIO.read(new URL(var1)).getScaledInstance(var2, var2, 4);
      } catch (Exception var3) {
         Utils.error("Could not load your custom map image with the url: " + var1 + ". Try a different image or a different image hosting site.");
      }

   }

   public void render(MapView var1, MapCanvas var2, Player var3) {
      int var4 = 1;

      for(String var6 : this.texts) {
         var2.drawText(25, var4++ * 10, MinecraftFont.Font, var6);
      }

      if (this.image != null) {
         if (!this.texts.isEmpty()) {
            var2.drawImage(32, var4 * 10 + 10, this.image);
         } else {
            var2.drawImage(0, 0, this.image);
         }
      }

   }
}
