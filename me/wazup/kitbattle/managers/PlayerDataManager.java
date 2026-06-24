package me.wazup.kitbattle.managers;

import java.util.HashMap;
import java.util.UUID;
import me.wazup.kitbattle.PlayerData;
import me.wazup.kitbattle.utils.Utils;
import org.bukkit.entity.Player;

public class PlayerDataManager {
   private static final HashMap<UUID, PlayerData> playerData = new HashMap();

   public static void load(Player var0) {
      playerData.put(var0.getUniqueId(), new PlayerData(var0));
   }

   public static void loadAll() {
      for(Player var1 : Utils.getOnlinePlayers()) {
         playerData.put(var1.getUniqueId(), new PlayerData(var1));
      }

   }

   public static void remove(Player var0) {
      playerData.remove(var0.getUniqueId());
   }

   public static PlayerData get(Player var0) {
      return (PlayerData)playerData.get(var0.getUniqueId());
   }

   public static HashMap<UUID, PlayerData> getAll() {
      return playerData;
   }
}
