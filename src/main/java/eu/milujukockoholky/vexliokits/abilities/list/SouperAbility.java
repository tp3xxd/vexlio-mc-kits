package eu.milujukockoholky.vexliokits.abilities.list;

import java.util.ArrayList;
import java.util.Iterator;
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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class SouperAbility extends Ability {
   int cooldown;
   Material activationMaterial;

   public SouperAbility() {
      this.activationMaterial = XMaterial.CHEST.parseMaterial();
   }

   public String getName() {
      return "Souper";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Souper.Cooldown");
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
      if (var2.hasCooldown(var1, "Souper")) {
         return false;
      } else {
         var2.setCooldown(var1, "Souper", this.cooldown, true);
         final VexlioKits var4 = VexlioKits.getInstance();
         var4.sendUseAbility(var1, var2);
         final PlayerInventory var5 = var1.getInventory();
         final ArrayList var6 = new ArrayList();

         for(int var7 = 0; var7 < 9; ++var7) {
            if (var5.getItem(var7) == null || var5.getItem(var7).getType().equals(Material.BOWL)) {
               var6.add(var7);
            }
         }

         if (var6.size() > 0) {
            int var10 = 0;

            for(int var8 = 9; var8 < 36; ++var8) {
               if (var5.getItem(var8) != null && var5.getItem(var8).getType().equals(var4.listen.soup.getType())) {
                  var5.getItem(var8).setType(Material.BOWL);
                  ++var10;
                  if (var10 == var6.size()) {
                     break;
                  }
               }
            }

            int var11 = var6.size() - var10;

            for(int var9 = 0; var9 < var11; ++var9) {
               var6.remove(var6.size() - 1);
            }

            (new BukkitRunnable() {
               final Iterator<Integer> iterator = var6.iterator();

               public void run() {
                  if (this.iterator.hasNext() && var2.getKit() != null) {
                     var5.setItem((Integer)this.iterator.next(), var4.listen.soup);
                     if (var1 != null) {
                        var1.playSound(var1.getLocation(), SoundsManager.CLICK, 1.0F, 1.0F);
                     }

                  } else {
                     this.cancel();
                  }
               }
            }).runTaskTimer(var4, 0L, 4L);
         }

         return true;
      }
   }
}
