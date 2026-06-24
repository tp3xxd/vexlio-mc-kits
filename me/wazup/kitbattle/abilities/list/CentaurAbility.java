package me.wazup.kitbattle.abilities.list;

import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.abilities.Ability;
import me.wazup.kitbattle.managers.PlayerDataManager;
import me.wazup.kitbattle.managers.SoundsManager;
import me.wazup.kitbattle.utils.Utils;
import me.wazup.kitbattle.utils.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CentaurAbility extends Ability {
   int cooldown;
   int range;
   int damage;
   PotionEffect regenEffect;
   Material activationMaterial;

   public CentaurAbility() {
      this.regenEffect = new PotionEffect(PotionEffectType.REGENERATION, 200, 0);
      this.activationMaterial = XMaterial.GUNPOWDER.parseMaterial();
   }

   public String getName() {
      return "Centaur";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Centaur.Cooldown");
      this.range = var1.getInt("Abilities.Centaur.Damage-Radius");
      this.damage = var1.getInt("Abilities.Centaur.Damage");
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
      if (var2.hasCooldown(var1, "Centaur")) {
         return false;
      } else {
         var2.setCooldown(var1, "Centaur", this.cooldown, true);
         Kitbattle var4 = Kitbattle.getInstance();
         var4.sendUseAbility(var1, var2);
         var1.getWorld().createExplosion(var1.getLocation(), 0.0F, false);
         var1.playSound(var1.getLocation(), SoundsManager.BLAZE_DEATH, 1.0F, 1.0F);
         var1.damage((double)this.damage * (double)0.5F);

         for(Location var6 : Utils.getSurroundingLocations(var1.getLocation())) {
            var1.getWorld().createExplosion(var6, 0.0F, false);
         }

         for(Entity var9 : var1.getNearbyEntities((double)this.range, (double)this.range, (double)this.range)) {
            if (var9 instanceof Damageable) {
               if (!var9.getType().equals(EntityType.PLAYER) || PlayerDataManager.get((Player)var9).getKit() != null && !var2.getMap().isInSpawn((Player)var9)) {
                  ((Damageable)var9).damage((double)this.damage, var1);
                  if (var9.getType().equals(EntityType.PLAYER)) {
                     Player var7 = (Player)var9;
                     var7.addPotionEffect(this.regenEffect);
                     var7.sendMessage(((String)var4.msgs.messages.get("Centaur-Strike")).replace("%player%", var1.getName()));
                     var7.playSound(var1.getLocation(), SoundsManager.BLAZE_DEATH, 1.0F, 1.0F);
                  }
               } else {
                  var1.sendMessage((String)var4.msgs.messages.get("Use-Ability-Deny"));
               }
            }
         }

         return true;
      }
   }
}
