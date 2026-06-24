package eu.milujukockoholky.vexliokits.abilities.list;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import eu.milujukockoholky.vexliokits.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.metadata.FixedMetadataValue;

public class SummonerAbility extends Ability {
   int cooldown;
   int duration;
   Material activationMaterial;

   public SummonerAbility() {
      this.activationMaterial = XMaterial.IRON_BLOCK.parseMaterial();
   }

   public String getName() {
      return "Summoner";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Summoner.Cooldown");
      this.duration = var1.getInt("Abilities.Summoner.Golem-Lasts-For") * 20;
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
      if (var2.hasCooldown(var1, "Summoner")) {
         return false;
      } else {
         var2.setCooldown(var1, "Summoner", this.cooldown, true);
         final VexlioKits var4 = VexlioKits.getInstance();
         var4.sendUseAbility(var1, var2);
         final IronGolem var5 = (IronGolem)var1.getWorld().spawnEntity(var1.getLocation(), EntityType.IRON_GOLEM);
         var5.setCustomName(var1.getName() + "'s Golem");
         var5.setPassenger(var1);
         var5.setMetadata("toRemove", new FixedMetadataValue(var4, true));

         for(Entity var7 : var1.getNearbyEntities((double)10.0F, (double)10.0F, (double)10.0F)) {
            if (var7.getType().equals(EntityType.PLAYER)) {
               var5.setTarget((LivingEntity)var7);
               break;
            }
         }

         var4.toRemove.add(var5);
         Bukkit.getScheduler().scheduleSyncDelayedTask(var4, new Runnable() {
            public void run() {
               var4.toRemove.remove(var5);
               var5.remove();
            }
         }, (long)this.duration);
         return true;
      }
   }
}
