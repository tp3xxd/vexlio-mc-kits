package me.wazup.kitbattle.abilities;

import me.wazup.kitbattle.Kit;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.events.PlayerUseAbilityEvent;
import me.wazup.kitbattle.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AbilityListener implements Listener {
   private final Kitbattle plugin;

   public AbilityListener(Kitbattle var1) {
      this.plugin = var1;
   }

   @EventHandler
   public void onPlayerInteractEvent(PlayerInteractEvent var1) {
      Player var2 = var1.getPlayer();
      if (!var1.getAction().equals(Action.PHYSICAL)) {
         if (this.plugin.players.contains(var2.getUniqueId())) {
            PlayerData var3 = PlayerDataManager.get(var2);
            if (this.plugin.config.DisableInteractionsInSpawnRegion && var3.getMap().isInSpawn(var2)) {
               var1.setCancelled(true);
               return;
            }

            Kit var4 = var3.getKit();
            if (var4 == null || var4.getInteractionAbilities().isEmpty()) {
               return;
            }

            ItemStack var5 = var2.getItemInHand();
            if (var5 == null) {
               return;
            }

            Material var6 = var2.getItemInHand().getType();

            for(Ability var8 : var4.getInteractionAbilities()) {
               if (var8.getActivationMaterial().equals(var6)) {
                  if (var3.getMap().isInSpawn(var2)) {
                     var2.sendMessage((String)this.plugin.msgs.messages.get("Ability-Use-Deny"));
                     var1.setCancelled(true);
                     return;
                  }

                  var1.setCancelled(true);
                  boolean var9 = var8.execute(var2, var3, var1);
                  if (var8.getActivationProjectile() == null && var9) {
                     PlayerUseAbilityEvent var10 = new PlayerUseAbilityEvent(var2, var8.getName());
                     Bukkit.getPluginManager().callEvent(var10);
                  }
                  break;
               }
            }
         }

      }
   }

   @EventHandler
   public void AbilityPlayerInteractEntityEvent(PlayerInteractEntityEvent var1) {
      if (!var1.isCancelled()) {
         if (var1.getRightClicked().getType().equals(EntityType.PLAYER)) {
            Player var2 = var1.getPlayer();
            PlayerData var3 = PlayerDataManager.get(var2);
            Kit var4 = var3.getKit();
            if (var4 != null && !var3.getMap().isInSpawn(var2)) {
               Player var5 = (Player)var1.getRightClicked();
               PlayerData var6 = PlayerDataManager.get(var5);
               if (var6 != null) {
                  Kit var7 = var6.getKit();
                  if (var7 != null && !var3.getMap().isInSpawn(var5)) {
                     if (!var4.getEntityInteractionAbilities().isEmpty()) {
                        for(Ability var9 : var4.getEntityInteractionAbilities()) {
                           boolean var10 = var9.execute(var2, var3, var1);
                           if (var10) {
                              PlayerUseAbilityEvent var11 = new PlayerUseAbilityEvent(var2, var9.getName());
                              Bukkit.getPluginManager().callEvent(var11);
                           }
                        }
                     }

                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onEntityDamage(EntityDamageEvent var1) {
      if (var1.getEntityType().equals(EntityType.PLAYER)) {
         Player var2 = (Player)var1.getEntity();
         if (this.plugin.players.contains(var2.getUniqueId())) {
            PlayerData var3 = PlayerDataManager.get(var2);
            if (var3.getMap().isInSpawn(var2)) {
               var1.setCancelled(true);
               return;
            }

            Kit var4 = var3.getKit();
            if (var4 == null) {
               return;
            }

            if (!var4.getDamageAbilities().isEmpty()) {
               for(Ability var6 : var4.getDamageAbilities()) {
                  boolean var7 = var6.execute(var2, var3, var1);
                  if (var7) {
                     PlayerUseAbilityEvent var8 = new PlayerUseAbilityEvent(var2, var6.getName());
                     Bukkit.getPluginManager().callEvent(var8);
                  }
               }
            }
         }
      }

   }

   @EventHandler
   public void onEntityDamageByEntity(EntityDamageByEntityEvent var1) {
      if (!var1.isCancelled()) {
         if (var1.getEntityType().equals(EntityType.PLAYER)) {
            Player var2 = (Player)var1.getEntity();
            if (this.plugin.players.contains(var2.getUniqueId())) {
               PlayerData var3 = PlayerDataManager.get(var2);
               Kit var4 = var3.getKit();
               if (var4 == null || var3.getMap().isInSpawn(var2)) {
                  return;
               }

               if (!var4.getAttackReceiveAbilities().isEmpty()) {
                  for(Ability var6 : var4.getAttackReceiveAbilities()) {
                     boolean var7 = var6.execute(var2, var3, var1);
                     if (var7) {
                        PlayerUseAbilityEvent var8 = new PlayerUseAbilityEvent(var2, var6.getName());
                        Bukkit.getPluginManager().callEvent(var8);
                     }
                  }
               }

               Entity var14 = var1.getDamager();
               if (var14.getType().equals(EntityType.PLAYER)) {
                  Player var15 = (Player)var14;
                  PlayerData var17 = PlayerDataManager.get(var15);
                  Kit var19 = var17.getKit();
                  if (var19 != null && !var19.getAttackAbilities().isEmpty()) {
                     for(Ability var10 : var19.getAttackAbilities()) {
                        boolean var11 = var10.execute(var15, var17, var1);
                        if (var11) {
                           PlayerUseAbilityEvent var12 = new PlayerUseAbilityEvent(var2, var10.getName());
                           Bukkit.getPluginManager().callEvent(var12);
                        }
                     }
                  }
               } else if (var14 instanceof Projectile) {
                  Projectile var16 = (Projectile)var14;
                  if (var16.getShooter() instanceof Player) {
                     Player var18 = (Player)var16.getShooter();
                     PlayerData var20 = PlayerDataManager.get(var18);
                     Kit var21 = var20.getKit();
                     if (var21 != null && !var21.getProjectileAbilities().isEmpty()) {
                        for(Ability var23 : var21.getProjectileAbilities()) {
                           if (var23.getActivationProjectile().equals(var14.getType())) {
                              boolean var24 = var23.execute(var18, var20, var1);
                              if (var24) {
                                 PlayerUseAbilityEvent var13 = new PlayerUseAbilityEvent(var2, var23.getName());
                                 Bukkit.getPluginManager().callEvent(var13);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }
}
