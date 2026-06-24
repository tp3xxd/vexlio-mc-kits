package eu.milujukockoholky.vexliokits.abilities.list;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import eu.milujukockoholky.vexliokits.managers.SoundsManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SwitcherAbility extends Ability {
   public String getName() {
      return "Switcher";
   }

   public void load(FileConfiguration var1) {
   }

   public Material getActivationMaterial() {
      return null;
   }

   public EntityType getActivationProjectile() {
      return EntityType.SNOWBALL;
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
      VexlioKits var4 = VexlioKits.getInstance();
      EntityDamageByEntityEvent var5 = (EntityDamageByEntityEvent)var3;
      Player var6 = (Player)var5.getEntity();
      var4.sendUseAbility(var1, var2);
      Location var7 = var1.getLocation();
      var1.teleport(var6.getLocation());
      var6.teleport(var7);
      var1.playSound(var1.getLocation(), SoundsManager.PISTON_EXTEND, 1.0F, 1.0F);
      var6.playSound(var1.getLocation(), SoundsManager.PISTON_EXTEND, 1.0F, 1.0F);
      return true;
   }
}
