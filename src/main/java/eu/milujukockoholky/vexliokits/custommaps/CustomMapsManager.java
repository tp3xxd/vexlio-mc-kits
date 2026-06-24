package eu.milujukockoholky.vexliokits.custommaps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import eu.milujukockoholky.vexliokits.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class CustomMapsManager {
   private final HashMap<String, ItemStack> maps = new HashMap();
   private Method setMapId;
   private Method setMapView;

   public CustomMapsManager() {
      try {
         this.setMapView = MapMeta.class.getDeclaredMethod("setMapView", MapView.class);
      } catch (SecurityException | NoSuchMethodException var4) {
         try {
            this.setMapId = MapMeta.class.getDeclaredMethod("setMapId", Integer.TYPE);
         } catch (SecurityException | NoSuchMethodException var3) {
         }
      }

   }

   public void clear() {
      this.maps.clear();
   }

   public void registerMap(String var1, MapRenderer var2) {
      MapView var3 = Bukkit.createMap((World)Bukkit.getWorlds().get(0));

      for(MapRenderer var5 : var3.getRenderers()) {
         var3.removeRenderer(var5);
      }

      var3.addRenderer(var2);
      ItemStack var9 = XMaterial.FILLED_MAP.parseItem();
      if (this.setMapView == null && this.setMapId == null) {
         var9.setDurability((short)this.getViewId(var3));
      } else {
         MapMeta var10 = (MapMeta)var9.getItemMeta();
         if (this.setMapView != null) {
            try {
               this.setMapView.invoke(var10, var3);
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var8) {
               ((Exception)var8).printStackTrace();
            }
         } else {
            try {
               this.setMapId.invoke(var10, this.getViewId(var3));
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var7) {
               ((Exception)var7).printStackTrace();
            }
         }

         var9.setItemMeta(var10);
      }

      this.maps.put(var1, var9);
   }

   private int getViewId(MapView var1) {
      try {
         String var2 = MapView.class.getMethod("getId").invoke(var1).toString();
         return Integer.valueOf(var2);
      } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | IllegalAccessException var3) {
         ((Exception)var3).printStackTrace();
         return -1;
      }
   }

   public boolean isRegistered(String var1) {
      return this.maps.containsKey(var1);
   }

   public ItemStack getItemStack(String var1) {
      return (ItemStack)this.maps.get(var1);
   }
}
