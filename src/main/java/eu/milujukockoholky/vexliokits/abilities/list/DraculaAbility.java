package eu.milujukockoholky.vexliokits.abilities.list;

import java.util.Collection;
import java.util.HashMap;
import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import eu.milujukockoholky.vexliokits.abilities.AbilityManager;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
import eu.milujukockoholky.vexliokits.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DraculaAbility extends Ability {
   int cooldown;
   int duration;
   PotionEffect regenEffect;
   Material activationMaterial;

   public DraculaAbility() {
      this.regenEffect = new PotionEffect(PotionEffectType.REGENERATION, 20, 2);
      this.activationMaterial = XMaterial.REDSTONE.parseMaterial();
   }

   public String getName() {
      return "Dracula";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Dracula.Cooldown");
      this.duration = var1.getInt("Abilities.Dracula.Lasts-For");
      this.activationMaterial = ((XMaterial)XMaterial.matchXMaterial(var1.getString("Abilities." + this.getName() + ".Activation-Material")).get()).parseMaterial();
   }

   public Material getActivationMaterial() {
      return this.activationMaterial;
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

   public boolean execute(final Player var1, PlayerData var2, Event var3) {
      if (var3.getEventName().equals("EntityDamageByEntityEvent")) {
         var1.addPotionEffect(this.regenEffect);
         return false;
      } else if (var2.hasCooldown(var1, "Dracula")) {
         return false;
      } else {
         var2.setCooldown(var1, "Dracula", this.cooldown, true);
         VexlioKits var4 = VexlioKits.getInstance();
         var4.sendUseAbility(var1, var2);
         final HashMap<String, Collection<PotionEffect>> var5 = new HashMap<>();
         final HashMap var6 = new HashMap();

         for(Entity var8 : var1.getNearbyEntities((double)20.0F, (double)20.0F, (double)20.0F)) {
            if (var8.getType().equals(EntityType.PLAYER)) {
               Player var9 = (Player)var8;
               PlayerData var10 = PlayerDataManager.get(var9);
               if (var10 != null && var10.getKit() != null && !var2.getMap().isInSpawn(var9) && !var9.getActivePotionEffects().isEmpty() && !AbilityManager.getInstance().hasInteractionAbility(var9, "Dracula")) {
                  var5.put(var9.getName(), var9.getActivePotionEffects());
                  var6.put(var9.getName(), var10.getKit().getName());

                  for(PotionEffect var12 : var9.getActivePotionEffects()) {
                     var9.removePotionEffect(var12.getType());
                     var9.sendMessage(((String)var4.msgs.messages.get("Dracula-Suck-Warning")).replace("%seconds%", String.valueOf(this.duration)));
                     var1.addPotionEffect(var12);
                     var1.sendMessage(((String)var4.msgs.messages.get("Dracula-Receive-Effect")).replace("%effect%", var12.getType().getName()));
                  }
               }
            }
         }

         if (!var5.isEmpty()) {
            (new BukkitRunnable() {
               public void run() {
                  if (AbilityManager.getInstance().hasInteractionAbility(var1, "Dracula")) {
                     for(PotionEffect var2 : var1.getActivePotionEffects()) {
                        var1.removePotionEffect(var2.getType());
                     }
                  }

                  for(String var8 : var5.keySet()) {
                     Player var3 = Bukkit.getPlayer(var8);
                     if (var3 != null) {
                        PlayerData var4 = PlayerDataManager.get(var3);
                        if (var4.getKit() != null && var4.getKit().getName().equals(var6.get(var3.getName()))) {
                           for(PotionEffect var6x : var5.get(var8)) {
                              var3.addPotionEffect(var6x);
                           }
                        }
                     }
                  }

               }
            }).runTaskLater(var4, (long)(this.duration * 20));
         }

         return true;
      }
   }
}
