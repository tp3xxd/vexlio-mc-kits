package eu.milujukockoholky.vexliokits.abilities.list;

import java.util.ArrayList;
import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import eu.milujukockoholky.vexliokits.utils.Utils;
import eu.milujukockoholky.vexliokits.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class BurrowerAbility extends Ability {
   int cooldown;
   int duration;
   Material activationMaterial;

   public BurrowerAbility() {
      this.activationMaterial = Material.BRICK;
   }

   public String getName() {
      return "Burrower";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Burrower.Cooldown");
      this.duration = var1.getInt("Abilities.Burrower.Room-Lasts-For") * 20;
      this.activationMaterial = ((XMaterial)XMaterial.matchXMaterial(var1.getString("Abilities." + this.getName() + ".Activation-Material")).get()).parseMaterial();
   }

   public Material getActivationMaterial() {
      return this.activationMaterial;
   }

   public EntityType getActivationProjectile() {
      return null;
   }

   public boolean isAttackActivated() {
      return false;
   }

   public boolean isAttackReceiveActivated() {
      return false;
   }

   public boolean isDamageActivated() {
      return false;
   }

   public boolean isEntityInteractionActivated() {
      return false;
   }

   public boolean execute(Player var1, PlayerData var2, Event var3) {
      if (var2.hasCooldown(var1, "Burrower")) {
         return false;
      } else {
         var2.setCooldown(var1, "Burrower", this.cooldown, true);
         final VexlioKits var4 = VexlioKits.getInstance();
         var4.sendUseAbility(var1, var2);
         ArrayList<Location> var5 = Utils.getRoomLocations(var1.getLocation());
         boolean var6 = true;

         for(Location var8 : var5) {
            if (var8.getBlock().getType() != Material.AIR) {
               var6 = false;
               break;
            }
         }

         if (var6) {
            final ArrayList<BlockState> var11 = new ArrayList<>();
            Material var12 = XMaterial.BRICKS.parseMaterial();

            for(Location var10 : var5) {
               var11.add(var10.getBlock().getState());
               var10.getBlock().setType(var12);
            }

            ((Location)var5.get(0)).getBlock().setType(Material.GLOWSTONE);
            var4.toRollback.addAll(var11);
            var1.teleport(var1.getLocation().add((double)0.0F, (double)10.0F, (double)0.0F));
            Bukkit.getScheduler().scheduleSyncDelayedTask(var4, new Runnable() {
               public void run() {
                  for(BlockState var2 : var11) {
                     Utils.Rollback(var2);
                  }

                  var4.toRollback.removeAll(var11);
               }
            }, (long)this.duration);
         } else {
            var1.sendMessage((String)var4.msgs.messages.get("Burrower-No-Space"));
         }

         return true;
      }
   }
}
