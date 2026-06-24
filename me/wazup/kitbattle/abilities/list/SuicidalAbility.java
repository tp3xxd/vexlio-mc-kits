package me.wazup.kitbattle.abilities.list;

import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.abilities.Ability;
import me.wazup.kitbattle.utils.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.metadata.FixedMetadataValue;

public class SuicidalAbility extends Ability {
   int cooldown;
   Material activationMaterial;

   public SuicidalAbility() {
      this.activationMaterial = XMaterial.REDSTONE_TORCH.parseMaterial();
   }

   public String getName() {
      return "Suicidal";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Suicidal.Cooldown");
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
      if (var2.hasCooldown(var1, "Suicidal")) {
         return false;
      } else {
         var2.setCooldown(var1, "Suicidal", this.cooldown, true);
         Kitbattle var4 = Kitbattle.getInstance();
         var4.sendUseAbility(var1, var2);

         for(Entity var6 : var1.getNearbyEntities((double)5.0F, (double)5.0F, (double)5.0F)) {
            if (var6 instanceof Damageable) {
               ((Damageable)var6).damage((double)1.0F, var1);
            }
         }

         for(int var7 = 0; var7 < 2; ++var7) {
            TNTPrimed var8 = (TNTPrimed)var1.getWorld().spawn(var1.getLocation().add((double)0.0F, (double)1.0F, (double)0.0F), TNTPrimed.class);
            var8.setFuseTicks(1);
            var8.setMetadata("tnts", new FixedMetadataValue(var4, true));
         }

         return true;
      }
   }
}
