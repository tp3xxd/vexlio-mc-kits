package me.wazup.kitbattle.abilities;

import me.wazup.kitbattle.PlayerData;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class Ability {
   public abstract String getName();

   public abstract void load(FileConfiguration var1);

   public abstract Material getActivationMaterial();

   public abstract EntityType getActivationProjectile();

   public abstract boolean isAttackActivated();

   public abstract boolean isAttackReceiveActivated();

   public abstract boolean isDamageActivated();

   public abstract boolean isEntityInteractionActivated();

   public abstract boolean execute(Player var1, PlayerData var2, Event var3);
}
