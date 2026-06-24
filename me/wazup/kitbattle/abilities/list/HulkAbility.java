package me.wazup.kitbattle.abilities.list;

import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.abilities.Ability;
import me.wazup.kitbattle.managers.PlayerDataManager;
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
import org.bukkit.util.Vector;

public class HulkAbility extends Ability {
   int cooldown;
   int damage;
   int radius;
   Material activationMaterial;

   public HulkAbility() {
      this.activationMaterial = XMaterial.STICKY_PISTON.parseMaterial();
   }

   public String getName() {
      return "Hulk";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Hulk.Cooldown");
      this.damage = var1.getInt("Abilities.Hulk.Damage-Dealt") * 2;
      this.radius = var1.getInt("Abilities.Hulk.Damage-Radius");
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
      if (var2.hasCooldown(var1, "Hulk")) {
         return false;
      } else {
         var2.setCooldown(var1, "Hulk", this.cooldown, true);
         Kitbattle var4 = Kitbattle.getInstance();
         var4.sendUseAbility(var1, var2);
         var1.getWorld().createExplosion(var1.getLocation(), 0.0F, false);

         for(Location var6 : Utils.getSurroundingLocations(var1.getLocation())) {
            var1.getWorld().createExplosion(var6, 0.0F, false);
         }

         for(Entity var9 : var1.getNearbyEntities((double)this.radius, (double)this.radius, (double)this.radius)) {
            if (var9 instanceof Damageable) {
               if (!var9.getType().equals(EntityType.PLAYER) || PlayerDataManager.get((Player)var9).getKit() != null && !var2.getMap().isInSpawn((Player)var9)) {
                  ((Damageable)var9).damage((double)this.damage, var1);
                  Vector var7 = var9.getType().equals(EntityType.PLAYER) ? ((Player)var9).getEyeLocation().getDirection() : var9.getVelocity();
                  var7.multiply(-3);
                  var7.setY((double)1.0F);
                  var9.setVelocity(var7);
               } else {
                  var1.sendMessage((String)var4.msgs.messages.get("Use-Ability-Deny"));
               }
            }
         }

         return true;
      }
   }
}
