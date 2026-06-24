package me.wazup.kitbattle.abilities.list;

import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.abilities.Ability;
import me.wazup.kitbattle.managers.PlayerDataManager;
import me.wazup.kitbattle.utils.XMaterial;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;

public class DragonAbility extends Ability {
   int cooldown;
   int bursts;
   int damage;
   int fireDuration;
   int fireRange;
   Material activationMaterial;

   public DragonAbility() {
      this.activationMaterial = XMaterial.FIRE_CHARGE.parseMaterial();
   }

   public String getName() {
      return "Dragon";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Dragon.Cooldown");
      this.bursts = var1.getInt("Abilities.Dragon.Amount-Of-Bursts");
      this.damage = var1.getInt("Abilities.Dragon.Damage-Dealt") * 2;
      this.fireDuration = var1.getInt("Abilities.Dragon.Fire-Lasts-For") * 20;
      this.fireRange = var1.getInt("Abilities.Dragon.Fire-Range");
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

   public boolean execute(final Player var1, final PlayerData var2, Event var3) {
      if (var2.hasCooldown(var1, "Dragon")) {
         return false;
      } else {
         var2.setCooldown(var1, "Dragon", this.cooldown, true);
         final Kitbattle var4 = Kitbattle.getInstance();
         var4.sendUseAbility(var1, var2);
         (new BukkitRunnable() {
            int bursts = 0;

            public void run() {
               Location[] var1x = new Location[]{var1.getLocation(), var1.getLocation().clone().add((double)0.0F, (double)DragonAbility.this.fireRange, (double)0.0F), var1.getLocation().clone().add((double)0.0F, (double)(-DragonAbility.this.fireRange), (double)0.0F), var1.getLocation().clone().add((double)DragonAbility.this.fireRange, (double)0.0F, (double)0.0F), var1.getLocation().clone().add((double)0.0F, (double)0.0F, (double)DragonAbility.this.fireRange), var1.getLocation().clone().add((double)DragonAbility.this.fireRange, (double)0.0F, (double)DragonAbility.this.fireRange), var1.getLocation().clone().add((double)(-DragonAbility.this.fireRange), (double)0.0F, (double)(-DragonAbility.this.fireRange)), var1.getLocation().clone().add((double)DragonAbility.this.fireRange, (double)0.0F, (double)(-DragonAbility.this.fireRange)), var1.getLocation().clone().add((double)(-DragonAbility.this.fireRange), (double)0.0F, (double)DragonAbility.this.fireRange)};

               for(Location var5 : var1x) {
                  var1.getWorld().playEffect(var5, Effect.MOBSPAWNER_FLAMES, 1);
               }

               for(Entity var7 : var1.getNearbyEntities((double)DragonAbility.this.fireRange, (double)DragonAbility.this.fireRange, (double)DragonAbility.this.fireRange)) {
                  if (var7 instanceof Damageable) {
                     if (var7.getType().equals(EntityType.PLAYER)) {
                        Player var8 = (Player)var7;
                        if (var8.getName().equals(var1.getName())) {
                           continue;
                        }

                        PlayerData var9 = PlayerDataManager.get(var8);
                        if (var9 != null && PlayerDataManager.get(var8).getKit() == null || var2.getMap().isInSpawn(var8)) {
                           var1.sendMessage((String)var4.msgs.messages.get("Use-Ability-Deny"));
                           continue;
                        }
                     }

                     ((Damageable)var7).damage((double)DragonAbility.this.damage, var1);
                     var7.setFireTicks(DragonAbility.this.fireDuration);
                  }
               }

               ++this.bursts;
               if (this.bursts >= this.bursts) {
                  this.cancel();
               }

            }
         }).runTaskTimer(var4, 0L, 20L);
         return true;
      }
   }
}
