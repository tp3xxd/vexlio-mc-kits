package me.wazup.kitbattle.abilities.list;

import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.abilities.Ability;
import me.wazup.kitbattle.managers.PlayerDataManager;
import me.wazup.kitbattle.managers.SoundsManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class StomperAbility extends Ability {
   int radius;
   int maxFallDamage;
   int maxDamage;

   public String getName() {
      return "Stomper";
   }

   public void load(FileConfiguration var1) {
      this.radius = var1.getInt("Abilities.Stomper.Stomp-Radius");
      this.maxFallDamage = var1.getInt("Abilities.Stomper.Max-Fall-Damage") * 2;
      this.maxDamage = var1.getInt("Abilities.Stomper.Max-Damage-Dealt-When-Stomped-While-Shifting") * 2;
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
      return true;
   }

   public boolean isEntityInteractionActivated() {
      return false;
   }

   public boolean execute(Player var1, PlayerData var2, Event var3) {
      EntityDamageEvent var4 = (EntityDamageEvent)var3;
      if (var4.getCause().equals(DamageCause.FALL)) {
         Kitbattle var5 = Kitbattle.getInstance();
         double var6 = var4.getDamage();
         if (var6 > (double)this.maxFallDamage) {
            var4.setDamage((double)this.maxFallDamage);
         }

         var1.playSound(var1.getLocation(), SoundsManager.ANVIL_LAND, 1.0F, 1.0F);

         for(Entity var9 : var1.getNearbyEntities((double)this.radius, (double)this.radius, (double)this.radius)) {
            if (var9 instanceof Damageable) {
               if (var9 instanceof Player) {
                  Player var10 = (Player)var9;
                  if (PlayerDataManager.get(var10).getKit() != null && !var2.getMap().isInSpawn(var10)) {
                     var10.playSound(var10.getLocation(), SoundsManager.ANVIL_LAND, 1.0F, 1.0F);
                     if (!var10.isSneaking()) {
                        var10.damage(var6, var1);
                     } else if (var6 > (double)this.maxDamage) {
                        var10.damage((double)this.maxDamage);
                     } else {
                        var10.damage(var6, var1);
                     }
                  } else {
                     var1.sendMessage((String)var5.msgs.messages.get("Use-Ability-Deny"));
                  }
               } else {
                  ((Damageable)var9).damage(var6, var1);
               }
            }
         }
      }

      return false;
   }
}
