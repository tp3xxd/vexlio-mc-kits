package eu.milujukockoholky.vexliokits.abilities.list;

import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import eu.milujukockoholky.vexliokits.managers.SoundsManager;
import eu.milujukockoholky.vexliokits.utils.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;

public class PhantomAbility extends Ability {
   int cooldown;
   int duration;
   Material activationMaterial;

   public PhantomAbility() {
      this.activationMaterial = XMaterial.BOOK.parseMaterial();
   }

   public String getName() {
      return "Phantom";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Phantom.Cooldown");
      this.duration = var1.getInt("Abilities.Phantom.Flight-Lasts-For");
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
      if (var2.hasCooldown(var1, "Phantom")) {
         return false;
      } else {
         var2.setCooldown(var1, "Phantom", this.cooldown, true);
         final VexlioKits var4 = VexlioKits.getInstance();

         for(Entity var6 : var1.getNearbyEntities((double)50.0F, (double)50.0F, (double)50.0F)) {
            if (var6.getType().equals(EntityType.PLAYER)) {
               ((Player)var6).playSound(var6.getLocation(), SoundsManager.WITHER_SPAWN, 1.0F, 1.0F);
               var6.sendMessage(((String)var4.msgs.messages.get("Phantom-Not-Hacking")).replace("%player%", var1.getName()));
            }
         }

         var1.playSound(var1.getLocation(), SoundsManager.WITHER_SPAWN, 1.0F, 1.0F);
         var1.setAllowFlight(true);
         var4.sendUseAbility(var1, var2);
         (new BukkitRunnable() {
            int Seconds;

            {
               this.Seconds = PhantomAbility.this.duration;
            }

            public void run() {
               if (var1.isOnline() && var2.getKit() != null) {
                  if (this.Seconds == 0) {
                     var1.setAllowFlight(false);
                     this.cancel();
                  } else {
                     var1.sendMessage(((String)var4.msgs.messages.get("Phantom-Fly-Time-Left")).replace("%seconds%", String.valueOf(this.Seconds)));
                     --this.Seconds;
                  }
               } else {
                  this.cancel();
               }
            }
         }).runTaskTimer(var4, 0L, 20L);
         return true;
      }
   }
}
