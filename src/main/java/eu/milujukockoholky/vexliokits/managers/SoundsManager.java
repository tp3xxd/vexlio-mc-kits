package eu.milujukockoholky.vexliokits.managers;

import org.bukkit.Sound;

public class SoundsManager {
   public static Sound WITHER_SHOOT;
   public static Sound CLICK;
   public static Sound WITHER_SPAWN;
   public static Sound ANVIL_LAND;
   public static Sound ITEM_PICKUP;
   public static Sound NOTE_PLING;
   public static Sound PISTON_EXTEND;
   public static Sound BLAZE_DEATH;
   public static Sound ENDERMAN_DEATH;
   public static Sound ENDERMAN_SCREAM;
   public static Sound IRONGOLEM_DEATH;

   public static void loadSounds() {
      try {
         WITHER_SHOOT = Sound.valueOf("WITHER_SHOOT");
         CLICK = Sound.valueOf("CLICK");
         WITHER_SPAWN = Sound.valueOf("WITHER_SPAWN");
         ANVIL_LAND = Sound.valueOf("ANVIL_LAND");
         ITEM_PICKUP = Sound.valueOf("ITEM_PICKUP");
         NOTE_PLING = Sound.valueOf("NOTE_PLING");
         PISTON_EXTEND = Sound.valueOf("PISTON_EXTEND");
         BLAZE_DEATH = Sound.valueOf("BLAZE_DEATH");
         ENDERMAN_DEATH = Sound.valueOf("ENDERMAN_DEATH");
         ENDERMAN_SCREAM = Sound.valueOf("ENDERMAN_SCREAM");
         IRONGOLEM_DEATH = Sound.valueOf("IRONGOLEM_DEATH");
      } catch (IllegalArgumentException var3) {
         try {
            WITHER_SHOOT = Sound.valueOf("ENTITY_WITHER_SHOOT");
            CLICK = Sound.valueOf("UI_BUTTON_CLICK");
            WITHER_SPAWN = Sound.valueOf("ENTITY_WITHER_SPAWN");
            ANVIL_LAND = Sound.valueOf("BLOCK_ANVIL_LAND");
            ITEM_PICKUP = Sound.valueOf("ENTITY_ITEM_PICKUP");
            NOTE_PLING = Sound.valueOf("BLOCK_NOTE_PLING");
            PISTON_EXTEND = Sound.valueOf("BLOCK_PISTON_EXTEND");
            BLAZE_DEATH = Sound.valueOf("ENTITY_BLAZE_DEATH");
            ENDERMAN_DEATH = Sound.valueOf("ENTITY_ENDERMEN_DEATH");
            ENDERMAN_SCREAM = Sound.valueOf("ENTITY_ENDERMEN_SCREAM");
            IRONGOLEM_DEATH = Sound.valueOf("ENTITY_IRONGOLEM_DEATH");
         } catch (IllegalArgumentException var2) {
            WITHER_SHOOT = Sound.valueOf("ENTITY_WITHER_SHOOT");
            CLICK = Sound.valueOf("UI_BUTTON_CLICK");
            WITHER_SPAWN = Sound.valueOf("ENTITY_WITHER_SPAWN");
            ANVIL_LAND = Sound.valueOf("BLOCK_ANVIL_LAND");
            ITEM_PICKUP = Sound.valueOf("ENTITY_ITEM_PICKUP");
            NOTE_PLING = Sound.valueOf("BLOCK_NOTE_BLOCK_PLING");
            PISTON_EXTEND = Sound.valueOf("BLOCK_PISTON_EXTEND");
            BLAZE_DEATH = Sound.valueOf("ENTITY_BLAZE_DEATH");
            ENDERMAN_DEATH = Sound.valueOf("ENTITY_ENDERMAN_DEATH");
            ENDERMAN_SCREAM = Sound.valueOf("ENTITY_ENDERMAN_SCREAM");
            IRONGOLEM_DEATH = Sound.valueOf("ENTITY_IRON_GOLEM_DEATH");
         }
      }

   }
}
