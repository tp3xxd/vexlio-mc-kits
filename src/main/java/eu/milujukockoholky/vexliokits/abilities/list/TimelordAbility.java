package eu.milujukockoholky.vexliokits.abilities.list;

import java.util.HashMap;
import java.util.Map;
import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.abilities.Ability;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
import eu.milujukockoholky.vexliokits.managers.SoundsManager;
import eu.milujukockoholky.vexliokits.utils.XMaterial;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class TimelordAbility extends Ability {
   int cooldown;
   int maxRange;
   int duration;
   boolean freezeByTeleporation;
   Material activationMaterial;

   public TimelordAbility() {
      this.activationMaterial = XMaterial.CLOCK.parseMaterial();
   }

   public String getName() {
      return "Timelord";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Timelord.Cooldown");
      this.maxRange = var1.getInt("Abilities.Timelord.Freeze-Radius");
      this.duration = var1.getInt("Abilities.Timelord.Freeze-Time") * 2;
      this.freezeByTeleporation = var1.getBoolean("Abilities.Timelord.Freeze-By-Teleportation");
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

   public boolean execute(Player var1, final PlayerData var2, Event var3) {
      if (var2.hasCooldown(var1, "Timelord")) {
         return false;
      } else {
         var2.setCooldown(var1, "Timelord", this.cooldown, true);
         VexlioKits var4 = VexlioKits.getInstance();
         var1.playSound(var1.getLocation(), SoundsManager.WITHER_SHOOT, 1.0F, 1.0F);
         var4.sendUseAbility(var1, var2);
         final HashMap<Player, Location> var5 = new HashMap<>();

         for(Entity var7 : var1.getNearbyEntities((double)this.maxRange, (double)this.maxRange, (double)this.maxRange)) {
            if (var7.getType().equals(EntityType.PLAYER)) {
               Player var8 = (Player)var7;
               if (PlayerDataManager.get(var8).getKit() != null && !var2.getMap().isInSpawn(var8)) {
                  var8.getWorld().playEffect(var8.getLocation(), Effect.STEP_SOUND, 152);
                  var8.getWorld().playEffect(var8.getLocation().add((double)0.0F, (double)1.0F, (double)0.0F), Effect.STEP_SOUND, 152);
                  var8.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, this.duration * 10, 10));
                  var8.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, this.duration * 10, -10));
                  var8.playSound(var1.getLocation(), SoundsManager.WITHER_SHOOT, 1.0F, 1.0F);
                  if (this.freezeByTeleporation) {
                     var5.put(var8, var8.getLocation().getBlock().getLocation());
                  }
               } else {
                  var1.sendMessage((String)var4.msgs.messages.get("Use-Ability-Deny"));
               }
            }
         }

         if (this.freezeByTeleporation) {
            var5.put(var1, var1.getLocation().getBlock().getLocation());
         }

         if (!var5.isEmpty()) {
            (new BukkitRunnable() {
               int timer = 0;

               public void run() {
                  ++this.timer;
                  if (this.timer >= TimelordAbility.this.duration) {
                     this.cancel();
                     var5.clear();
                  } else {
                     for(Map.Entry<Player, Location> var2x : var5.entrySet()) {
                        Player var3 = var2x.getKey();
                        if ((var3.getLocation().getBlockX() != ((Location)var2x.getValue()).getBlockX() || var3.getLocation().getBlockZ() != ((Location)var2x.getValue()).getBlockZ()) && var3.hasPotionEffect(PotionEffectType.SLOW) && var3.hasPotionEffect(PotionEffectType.JUMP) && PlayerDataManager.get(var3).getKit() != null && !var2.getMap().isInSpawn(var3)) {
                           Location var4 = var3.getLocation();
                           var4.setX(((Location)var2x.getValue()).getX() + (double)0.5F);
                           var4.setZ(((Location)var2x.getValue()).getZ() + (double)0.5F);
                           var3.teleport(var4);
                        }
                     }
                  }

               }
            }).runTaskTimer(VexlioKits.getInstance(), 10L, 10L);
         }

         return true;
      }
   }
}
