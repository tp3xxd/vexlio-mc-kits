package eu.milujukockoholky.vexliokits.abilities.list;

import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import eu.milujukockoholky.vexliokits.utils.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

public class KangarooAbility extends Ability {
   int cooldown;
   Material activationMaterial;

   public KangarooAbility() {
      this.activationMaterial = XMaterial.FIREWORK_ROCKET.parseMaterial();
   }

   public String getName() {
      return "Kangaroo";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Kangaroo.Cooldown");
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
      if (var1.isOnGround()) {
         if (var2.hasCooldown(var1, "Kangaroo")) {
            return false;
         } else {
            var2.setCooldown(var1, "Kangaroo", this.cooldown, true);
            Vector var4 = var1.getEyeLocation().getDirection();
            if (var1.isSneaking()) {
               var4.setY(0.2);
               var4.multiply(4);
            } else {
               var4.setY(1.2);
            }

            var1.setVelocity(var4);
            return true;
         }
      } else {
         return false;
      }
   }
}
