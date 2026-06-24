package me.wazup.kitbattle.abilities.list;

import me.wazup.kitbattle.Kit;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.abilities.Ability;
import me.wazup.kitbattle.managers.PlayerDataManager;
import me.wazup.kitbattle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BuilderAbility extends Ability implements Listener {
   int duration;

   public String getName() {
      return "Builder";
   }

   public void load(FileConfiguration var1) {
      this.duration = var1.getInt("Abilities.Builder.Blocks-Last-For") * 20;
      Bukkit.getPluginManager().registerEvents(this, Kitbattle.getInstance());
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onBlockPlace(BlockPlaceEvent var1) {
      Player var2 = var1.getPlayer();
      if (Kitbattle.getInstance().players.contains(var2.getUniqueId())) {
         PlayerData var3 = PlayerDataManager.get(var2);
         Kit var4 = var3.getKit();
         if (var4 == null || !var4.getOtherAbilities().contains(this)) {
            return;
         }

         if (var3.getMap().isInSpawn(var2)) {
            var2.sendMessage((String)Kitbattle.getInstance().msgs.messages.get("Ability-Use-Deny"));
            return;
         }

         var1.setCancelled(false);
         BlockState var5 = var1.getBlockReplacedState();
         Kitbattle.getInstance().toRollback.add(var5);
         Bukkit.getScheduler().scheduleSyncDelayedTask(Kitbattle.getInstance(), () -> {
            Utils.Rollback(var5);
            Kitbattle.getInstance().toRollback.remove(var5);
         }, (long)this.duration);
      }

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
      return false;
   }

   public boolean execute(Player var1, PlayerData var2, Event var3) {
      return true;
   }
}
