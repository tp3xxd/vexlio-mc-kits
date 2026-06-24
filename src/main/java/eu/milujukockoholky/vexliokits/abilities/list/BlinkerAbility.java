package eu.milujukockoholky.vexliokits.abilities.list;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import eu.milujukockoholky.vexliokits.managers.SoundsManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class BlinkerAbility extends Ability {
   int cooldown;

   public String getName() {
      return "Blinker";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Blinker.Cooldown");
   }

   public Material getActivationMaterial() {
      return null;
   }

   public EntityType getActivationProjectile() {
      return EntityType.ARROW;
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
      EntityDamageByEntityEvent var4 = (EntityDamageByEntityEvent)var3;
      var4.setDamage((double)1.0F);
      if (var2.hasCooldown(var1, "Blinker")) {
         return false;
      } else {
         var2.setCooldown(var1, "Blinker", this.cooldown, true);
         Player var5 = (Player)var4.getEntity();
         var1.teleport(var5.getLocation());
         var5.damage((double)1.0F);
         TNTPrimed var6 = (TNTPrimed)var1.getWorld().spawn(var1.getLocation().add((double)0.0F, (double)1.0F, (double)0.0F), TNTPrimed.class);
         var6.setFuseTicks(30);
         var6.setMetadata("tnts", new FixedMetadataValue(VexlioKits.getInstance(), true));
         VexlioKits.getInstance().sendUseAbility(var1, var2);
         var1.playSound(var1.getLocation(), SoundsManager.ENDERMAN_DEATH, 1.0F, 1.0F);
         var5.playSound(var5.getLocation(), SoundsManager.ENDERMAN_DEATH, 1.0F, 1.0F);
         var5.sendMessage(((String)VexlioKits.getInstance().msgs.messages.get("Player-Blink")).replace("%player%", var1.getName()));
         return false;
      }
   }
}
