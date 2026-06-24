package me.wazup.kitbattle.abilities;

import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.events.PlayerUseAbilityEvent;
import me.wazup.kitbattle.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

public class SpecialAbilityListener implements Listener {
   private final Kitbattle plugin;

   public SpecialAbilityListener(Kitbattle var1) {
      this.plugin = var1;
   }

   @EventHandler
   public void AbilityFishEvent(PlayerFishEvent var1) {
      Player var2 = var1.getPlayer();
      if (var1.getState().equals(State.CAUGHT_ENTITY) && AbilityManager.getInstance().hasSpecialAbility(var2, "Fisherman")) {
         PlayerData var3 = PlayerDataManager.get(var2);
         if (!var1.getCaught().getType().equals(EntityType.PLAYER) || PlayerDataManager.get((Player)var1.getCaught()).getKit() != null && !var3.getMap().isInSpawn((Player)var1.getCaught())) {
            boolean var4 = AbilityManager.getInstance().getAbility("Fisherman").execute(var2, var3, var1);
            if (var4) {
               PlayerUseAbilityEvent var5 = new PlayerUseAbilityEvent(var2, "Fisherman");
               Bukkit.getPluginManager().callEvent(var5);
            }

         } else {
            var2.sendMessage((String)this.plugin.msgs.messages.get("Ability-Use-Deny"));
         }
      }
   }

   @EventHandler
   public void AbilityShootArrowEvent(ProjectileLaunchEvent var1) {
      if (var1.getEntity().getType() == EntityType.ARROW) {
         Projectile var2 = var1.getEntity();
         if (var2.getShooter() instanceof Player) {
            Player var3 = (Player)var2.getShooter();
            if (AbilityManager.getInstance().hasSpecialAbility(var3, "Climber")) {
               var1.setCancelled(true);
               PlayerData var4 = PlayerDataManager.get(var3);
               if (var4.getMap().isInSpawn(var3)) {
                  var3.sendMessage((String)this.plugin.msgs.messages.get("Ability-Use-Deny"));
                  var1.setCancelled(true);
                  return;
               }

               boolean var5 = AbilityManager.getInstance().getAbility("Climber").execute(var3, var4, var1);
               if (var5) {
                  PlayerUseAbilityEvent var6 = new PlayerUseAbilityEvent(var3, "Climber");
                  Bukkit.getPluginManager().callEvent(var6);
               }
            }

         }
      }
   }
}
