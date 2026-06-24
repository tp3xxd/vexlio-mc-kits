package eu.milujukockoholky.vexliokits.abilities.list;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
import eu.milujukockoholky.vexliokits.utils.XMaterial;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class ZenAbility extends Ability {
   int cooldown;
   int maxRange;
   Material activationMaterial;

   public ZenAbility() {
      this.activationMaterial = XMaterial.SLIME_BALL.parseMaterial();
   }

   public String getName() {
      return "Zen";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Zen.Cooldown");
      this.maxRange = var1.getInt("Abilities.Zen.Max-Range");
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
      if (var2.hasCooldown(var1, "Zen")) {
         return false;
      } else {
         var2.setCooldown(var1, "Zen", this.cooldown, true);
         VexlioKits var4 = VexlioKits.getInstance();
         var4.sendUseAbility(var1, var2);

         for(Entity var6 : var1.getNearbyEntities((double)this.maxRange, (double)this.maxRange, (double)this.maxRange)) {
            if (var6.getType() == EntityType.PLAYER) {
               Player var7 = (Player)var6;
               PlayerData var8 = PlayerDataManager.get(var7);
               if (var8 != null && var8.getKit() != null && !var2.getMap().isInSpawn(var7) && !var4.isInTournament(var7) && !var4.isInChallenge(var7)) {
                  var1.getWorld().playEffect(var1.getLocation(), Effect.ENDER_SIGNAL, 1);
                  var1.teleport(var7.getLocation());
                  var1.getWorld().playEffect(var1.getLocation(), Effect.ENDER_SIGNAL, 1);
                  return true;
               }
            }
         }

         var1.sendMessage((String)var4.msgs.messages.get("Zen-No-Players-Found"));
         return true;
      }
   }
}
