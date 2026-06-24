package eu.milujukockoholky.vexliokits.abilities.list;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import eu.milujukockoholky.vexliokits.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class RiderAbility extends Ability {
   int cooldown;
   int duration;
   Material activationMaterial;

   public RiderAbility() {
      this.activationMaterial = XMaterial.DIAMOND_HORSE_ARMOR.parseMaterial();
   }

   public String getName() {
      return "Rider";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Rider.Cooldown");
      this.duration = var1.getInt("Abilities.Rider.Horse-Lasts-For") * 20;
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
      if (var2.hasCooldown(var1, "Rider")) {
         return false;
      } else {
         var2.setCooldown(var1, "Rider", this.cooldown, true);
         final VexlioKits var4 = VexlioKits.getInstance();
         var4.sendUseAbility(var1, var2);
         final Horse var5 = (Horse)var1.getWorld().spawnEntity(var1.getLocation(), EntityType.HORSE);
         var5.setAdult();
         var5.setCustomName(var1.getName() + "'s Horse");
         var5.setOwner(var1);
         var5.setMaxHealth((double)40.0F);
         var5.setHealth((double)40.0F);
         var5.setMetadata("toRemove", new FixedMetadataValue(var4, true));
         var5.getInventory().setSaddle(new ItemStack(Material.SADDLE));
         var5.setPassenger(var1);
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
