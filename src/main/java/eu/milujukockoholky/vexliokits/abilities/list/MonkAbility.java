package eu.milujukockoholky.vexliokits.abilities.list;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import eu.milujukockoholky.vexliokits.utils.Utils;
import eu.milujukockoholky.vexliokits.utils.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class MonkAbility extends Ability {
   int cooldown;
   Material activationMaterial;

   public MonkAbility() {
      this.activationMaterial = XMaterial.BLAZE_ROD.parseMaterial();
   }

   public String getName() {
      return "Monk";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Monk.Cooldown");
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
         if (var2.hasCooldown(var1, "Monk")) {
            return false;
         } else {
            var2.setCooldown(var1, "Monk", this.cooldown, true);
            VexlioKits var4 = VexlioKits.getInstance();
            Player var5 = (Player)((PlayerInteractEntityEvent)var3).getRightClicked();
            var4.sendUseAbility(var1, var2);
            int var6 = var5.getInventory().getHeldItemSlot();

            int var7;
            for(var7 = Utils.random.nextInt(9); var6 == var7; var7 = Utils.random.nextInt(9)) {
            }

            ItemStack var8 = var5.getItemInHand();
            ItemStack var9 = var5.getInventory().getItem(var7);
            var5.getInventory().setItem(var6, var9);
            var5.getInventory().setItem(var7, var8);
            var5.updateInventory();
            return true;
         }
      } else {
         return false;
      }
   }
}
