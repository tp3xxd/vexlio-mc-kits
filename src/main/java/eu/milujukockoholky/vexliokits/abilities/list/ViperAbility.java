package eu.milujukockoholky.vexliokits.abilities.list;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import eu.milujukockoholky.vexliokits.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ViperAbility extends Ability {
   int poisonChance;
   PotionEffect poisonEffect;

   public String getName() {
      return "Viper";
   }

   public void load(FileConfiguration var1) {
      this.poisonChance = Integer.valueOf(var1.getString("Abilities.Viper.Poison-Chance").replace("%", ""));
      this.poisonEffect = new PotionEffect(PotionEffectType.POISON, var1.getInt("Abilities.Viper.Poison-Lasts-For") * 20, var1.getInt("Abilities.Viper.Poison-Level") - 1);
   }

   public Material getActivationMaterial() {
      return null;
   }

   public EntityType getActivationProjectile() {
      return null;
   }

   public boolean isAttackActivated() {
      return true;
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
      int var4 = Utils.random.nextInt(100) + 1;
      if (var4 <= this.poisonChance) {
         Player var5 = (Player)((EntityDamageByEntityEvent)var3).getEntity();
         var5.removePotionEffect(PotionEffectType.POISON);
         var5.addPotionEffect(this.poisonEffect);
         VexlioKits.getInstance().sendUseAbility(var1, var2);
         return true;
      } else {
         return false;
      }
   }
}
