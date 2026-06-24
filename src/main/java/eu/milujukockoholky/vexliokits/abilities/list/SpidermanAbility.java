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
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class SpidermanAbility extends Ability {
   int cooldown;
   int duration;
   Material activationMaterial;
   EntityType activationProjectile;

   public SpidermanAbility() {
      this.activationMaterial = XMaterial.COBWEB.parseMaterial();
      this.activationProjectile = EntityType.SNOWBALL;
   }

   public String getName() {
      return "Spiderman";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Spiderman.Cooldown");
      this.duration = var1.getInt("Abilities.Spiderman.Webs-Last-For") * 20;
      this.activationMaterial = ((XMaterial)XMaterial.matchXMaterial(var1.getString("Abilities." + this.getName() + ".Activation-Material")).get()).parseMaterial();
   }

   public Material getActivationMaterial() {
      return this.activationMaterial;
   }

   public EntityType getActivationProjectile() {
      return this.activationProjectile;
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
      if (var3.getEventName().equals("PlayerInteractEvent")) {
         if (var2.hasCooldown(var1, "Spiderman")) {
            return false;
         } else {
            var2.setCooldown(var1, "Spiderman", this.cooldown, true);
            VexlioKits.getInstance().sendUseAbility(var1, var2);
            ((Snowball)var1.launchProjectile(Snowball.class)).setMetadata("spiderman", new FixedMetadataValue(VexlioKits.getInstance(), true));
            return true;
         }
      } else {
         EntityDamageByEntityEvent var4 = (EntityDamageByEntityEvent)var3;
         if (!var4.getDamager().hasMetadata("spiderman")) {
            return false;
         } else {
            final VexlioKits var5 = VexlioKits.getInstance();
            Player var6 = (Player)var4.getEntity();
            Location var7 = var6.getLocation();
            final ArrayList<BlockState> var8 = new ArrayList<>();
            Block var9 = var7.getBlock();
            if (var9.getType().equals(Material.WATER) || var9.getType().equals(Material.LAVA)) {
               var7.add((double)0.0F, (double)1.0F, (double)0.0F);
               var9 = var7.getBlock();
            }

            Material var10 = XMaterial.COBWEB.parseMaterial();

            for(Location var12 : Utils.getSurroundingLocations(var7)) {
               if (!var10.equals(var12.getBlock().getType())) {
                  var8.add(var12.getBlock().getState());
               }
            }

            if (!var10.equals(var9.getType())) {
               var8.add(var9.getState());
            }

            for(BlockState var14 : var8) {
               var5.toRollback.add(var14);
               var14.getBlock().setType(var10);
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(var5, new Runnable() {
               public void run() {
                  for(BlockState var2 : var8) {
                     var2.getWorld().createExplosion(var2.getLocation(), 0.0F, false);
                     Utils.Rollback(var2);
                     var5.toRollback.remove(var2);
                  }

               }
            }, (long)this.duration);
            return true;
         }
      }
   }
}
