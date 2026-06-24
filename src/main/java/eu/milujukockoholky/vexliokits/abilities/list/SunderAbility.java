package eu.milujukockoholky.vexliokits.abilities.list;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import eu.milujukockoholky.vexliokits.managers.SoundsManager;
import eu.milujukockoholky.vexliokits.utils.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class SunderAbility extends Ability {
   int cooldown;
   Material activationMaterial;

   public SunderAbility() {
      this.activationMaterial = XMaterial.BEACON.parseMaterial();
   }

   public String getName() {
      return "Sunder";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Sunder.Cooldown");
      this.activationMaterial = ((XMaterial)XMaterial.matchXMaterial(var1.getString("Abilities." + this.getName() + ".Activation-Material")).get()).parseMaterial();
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
      return true;
   }

   public boolean execute(Player var1, PlayerData var2, Event var3) {
      if (var1.getItemInHand() != null && var1.getItemInHand().getType().equals(this.activationMaterial)) {
         if (var2.hasCooldown(var1, "Sunder")) {
            return false;
         } else {
            var2.setCooldown(var1, "Sunder", this.cooldown, true);
            VexlioKits var4 = VexlioKits.getInstance();
            Player var5 = (Player)((PlayerInteractEntityEvent)var3).getRightClicked();
            var4.sendUseAbility(var1, var2);
            double var6 = var5.getHealth();
            var5.setHealth(var1.getHealth());
            var1.setHealth(var6);
            var5.sendMessage(((String)var4.msgs.messages.get("Sunder-Swap")).replace("%player%", var1.getName()));
            var5.playSound(var1.getLocation(), SoundsManager.IRONGOLEM_DEATH, 1.0F, 1.0F);
            var1.playSound(var1.getLocation(), SoundsManager.IRONGOLEM_DEATH, 1.0F, 1.0F);
            return true;
         }
      } else {
         return false;
      }
   }
}
