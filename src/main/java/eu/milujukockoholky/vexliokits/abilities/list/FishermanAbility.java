package eu.milujukockoholky.vexliokits.abilities.list;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerFishEvent;

public class FishermanAbility extends Ability {
   int cooldown;

   public String getName() {
      return "Fisherman";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Fisherman.Cooldown");
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
      return false;
   }

   public boolean isEntityInteractionActivated() {
      return false;
   }

   public boolean execute(Player var1, PlayerData var2, Event var3) {
      if (var2.hasCooldown(var1, "Fisherman")) {
         return false;
      } else {
         var2.setCooldown(var1, "Fisherman", this.cooldown, true);
         VexlioKits.getInstance().sendUseAbility(var1, var2);
         ((PlayerFishEvent)var3).getCaught().teleport(var1.getLocation());
         return true;
      }
   }
}
