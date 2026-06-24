package me.wazup.kitbattle.abilities.list;

import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.abilities.Ability;
import me.wazup.kitbattle.managers.SoundsManager;
import me.wazup.kitbattle.utils.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BaneAbility extends Ability {
   int cooldown;
   int duration;
   Material activationMaterial;

   public BaneAbility() {
      this.activationMaterial = XMaterial.GOLDEN_HOE.parseMaterial();
   }

   public String getName() {
      return "Bane";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Bane.Cooldown");
      this.duration = var1.getInt("Abilities.Bane.Duration") * 20;
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
         if (var2.hasCooldown(var1, "Bane")) {
            return false;
         } else {
            var2.setCooldown(var1, "Bane", this.cooldown, true);
            Kitbattle var4 = Kitbattle.getInstance();
            Player var5 = (Player)((PlayerInteractEntityEvent)var3).getRightClicked();
            var4.sendUseAbility(var1, var2);
            var5.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.duration, 3));
            var5.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, this.duration, 3));
            var5.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, this.duration, 3));
            var5.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, this.duration, 2));
            var5.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, this.duration, 2));
            var5.sendMessage(((String)var4.msgs.messages.get("Bane-Curse")).replace("%player%", var1.getName()));
            var5.playSound(var1.getLocation(), SoundsManager.ENDERMAN_SCREAM, 1.0F, 1.0F);
            var1.playSound(var1.getLocation(), SoundsManager.ENDERMAN_SCREAM, 1.0F, 1.0F);
            return true;
         }
      } else {
         return false;
      }
   }
}
