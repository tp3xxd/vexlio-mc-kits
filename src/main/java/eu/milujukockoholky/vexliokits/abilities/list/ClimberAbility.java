package eu.milujukockoholky.vexliokits.abilities.list;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class ClimberAbility extends Ability {
   int cooldown;
   int duration;

   public String getName() {
      return "Climber";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Climber.Cooldown");
      this.duration = var1.getInt("Abilities.Climber.Time-Until-Chicken-Disappear") * 20;
   }

   public Material getActivationMaterial() {
      return null;
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
      if (var2.hasCooldown(var1, "Climber")) {
         return false;
      } else {
         var2.setCooldown(var1, "Climber", this.cooldown, true);
         final VexlioKits var4 = VexlioKits.getInstance();
         ProjectileLaunchEvent var5 = (ProjectileLaunchEvent)var3;
         final Chicken var6 = (Chicken)var1.getWorld().spawnEntity(var1.getLocation(), EntityType.CHICKEN);
         var6.setVelocity(var5.getEntity().getVelocity().multiply(2));
         var6.setPassenger(var1);
         var6.setMetadata("toRemove", new FixedMetadataValue(var4, true));
         var4.sendUseAbility(var1, var2);
         var4.toRemove.add(var6);
         Bukkit.getScheduler().scheduleSyncDelayedTask(var4, new Runnable() {
            public void run() {
               var4.toRemove.remove(var6);
               var6.remove();
            }
         }, (long)this.duration);
         return true;
      }
   }
}
