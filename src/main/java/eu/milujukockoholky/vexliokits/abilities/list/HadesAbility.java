package eu.milujukockoholky.vexliokits.abilities.list;

import java.util.ArrayList;
import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import eu.milujukockoholky.vexliokits.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HadesAbility extends Ability {
   int cooldown;
   int amountOfDogs;
   int duration;
   Material activationMaterial;

   public HadesAbility() {
      this.activationMaterial = Material.BONE;
   }

   public String getName() {
      return "Hades";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Hades.Cooldown");
      this.amountOfDogs = var1.getInt("Abilities.Hades.Amount-Of-Dogs");
      this.duration = var1.getInt("Abilities.Hades.Dogs-Last-For") * 20;
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
      if (var2.hasCooldown(var1, "Hades")) {
         return false;
      } else {
         var2.setCooldown(var1, "Hades", this.cooldown, true);
         final VexlioKits var4 = VexlioKits.getInstance();
         var4.sendUseAbility(var1, var2);
         final ArrayList<Wolf> var5 = new ArrayList<>();

         for(int var6 = 0; var6 < this.amountOfDogs; ++var6) {
            Wolf var7 = (Wolf)var1.getWorld().spawnEntity(var1.getLocation(), EntityType.WOLF);
            var7.setCustomName(var1.getName() + "'s Wolf");
            var7.setOwner(var1);
            var7.setMaxHealth((double)20.0F);
            var7.setHealth((double)20.0F);
            var7.setMetadata("toRemove", new FixedMetadataValue(var4, true));
            var7.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 2));
            var5.add(var7);
         }

         var4.toRemove.addAll(var5);
         Bukkit.getScheduler().scheduleSyncDelayedTask(var4, new Runnable() {
            public void run() {
               for(Wolf var2 : var5) {
                  var2.remove();
               }

               var4.toRemove.removeAll(var5);
            }
         }, (long)this.duration);
         return true;
      }
   }
}
