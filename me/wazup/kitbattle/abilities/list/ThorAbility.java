package me.wazup.kitbattle.abilities.list;

import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.abilities.Ability;
import me.wazup.kitbattle.managers.PlayerDataManager;
import me.wazup.kitbattle.utils.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class ThorAbility extends Ability {
   int cooldown;
   int damage;
   int maxRange;
   Material activationMaterial;

   public ThorAbility() {
      this.activationMaterial = XMaterial.WOODEN_AXE.parseMaterial();
   }

   public String getName() {
      return "Thor";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Thor.Cooldown");
      this.damage = var1.getInt("Abilities.Thor.Lightning-Damage") * 2;
      this.maxRange = var1.getInt("Abilities.Thor.Strike-Radius");
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
      if (var2.hasCooldown(var1, "Thor")) {
         return false;
      } else {
         var2.setCooldown(var1, "Thor", this.cooldown, true);
         Kitbattle var4 = Kitbattle.getInstance();
         var4.sendUseAbility(var1, var2);

         for(Entity var6 : var1.getNearbyEntities((double)this.maxRange, (double)this.maxRange, (double)this.maxRange)) {
            if (var6 instanceof Damageable) {
               if (!(var6 instanceof Player) || PlayerDataManager.get((Player)var6).getKit() != null && !var2.getMap().isInSpawn((Player)var6)) {
                  var1.getWorld().strikeLightningEffect(var6.getLocation());
                  ((Damageable)var6).damage((double)this.damage, var1);
               } else {
                  var1.sendMessage((String)var4.msgs.messages.get("Use-Ability-Deny"));
               }
            }
         }

         return true;
      }
   }
}
