package me.wazup.kitbattle.abilities.list;

import java.util.ArrayList;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.abilities.Ability;
import me.wazup.kitbattle.utils.Utils;
import me.wazup.kitbattle.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class PrisonerAbility extends Ability {
   int cooldown;
   int duration;
   Material activationMaterial;
   EntityType activationProjectile;

   public PrisonerAbility() {
      this.activationMaterial = XMaterial.DISPENSER.parseMaterial();
      this.activationProjectile = EntityType.SNOWBALL;
   }

   public String getName() {
      return "Prisoner";
   }

   public void load(FileConfiguration var1) {
      this.cooldown = var1.getInt("Abilities.Prisoner.Cooldown");
      this.duration = var1.getInt("Abilities.Prisoner.Prison-Lasts-For") * 20;
      this.activationMaterial = ((XMaterial)XMaterial.matchXMaterial(var1.getString("Abilities." + this.getName() + ".Activation-Material")).get()).parseMaterial();
   }

   public Material getActivationMaterial() {
      return this.activationMaterial;
   }

   public EntityType getActivationProjectile() {
      return this.activationProjectile;
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
      if (var3.getEventName().equals("PlayerInteractEvent")) {
         if (var2.hasCooldown(var1, "Prisoner")) {
            return false;
         } else {
            var2.setCooldown(var1, "Prisoner", this.cooldown, true);
            Kitbattle.getInstance().sendUseAbility(var1, var2);
            ((Snowball)var1.launchProjectile(Snowball.class)).setMetadata("prison", new FixedMetadataValue(Kitbattle.getInstance(), true));
            return true;
         }
      } else {
         EntityDamageByEntityEvent var4 = (EntityDamageByEntityEvent)var3;
         if (!var4.getDamager().hasMetadata("prison")) {
            return false;
         } else {
            final Kitbattle var5 = Kitbattle.getInstance();
            Player var6 = (Player)var4.getEntity();
            ArrayList var7 = Utils.getCageBlocks(var6.getLocation().add((double)0.0F, (double)9.0F, (double)0.0F));
            boolean var8 = true;

            for(Block var10 : var7) {
               if (var10.getType() != Material.AIR) {
                  var1.sendMessage((String)var5.msgs.messages.get("Prisoner-No-Space"));
                  var8 = false;
                  break;
               }
            }

            if (!var8) {
               return false;
            } else {
               final ArrayList var12 = new ArrayList();

               for(Block var11 : var7) {
                  var12.add(var11.getState());
               }

               ((Block)var7.get(0)).setType(Material.MOSSY_COBBLESTONE);
               Material var14 = XMaterial.IRON_BARS.parseMaterial();

               for(int var15 = 1; var15 < 9; ++var15) {
                  ((Block)var7.get(var15)).setType(var14);
               }

               ((Block)var7.get(9)).setType(Material.MOSSY_COBBLESTONE);
               ((Block)var7.get(10)).setType(Material.LAVA);
               var6.damage((double)1.0F, var1);
               var6.teleport(var6.getLocation().add((double)0.0F, (double)9.0F, (double)0.0F));
               var5.toRollback.addAll(var12);
               Bukkit.getScheduler().scheduleSyncDelayedTask(var5, new Runnable() {
                  public void run() {
                     for(BlockState var2 : var12) {
                        Utils.Rollback(var2);
                     }

                     var5.toRollback.removeAll(var12);
                  }
               }, (long)this.duration);
               return true;
            }
         }
      }
   }
}
