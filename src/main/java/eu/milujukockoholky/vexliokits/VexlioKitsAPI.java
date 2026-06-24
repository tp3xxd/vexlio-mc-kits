package eu.milujukockoholky.vexliokits;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class VexlioKitsAPI {
   public static HashMap<String, String> getAllPlayersData() throws java.sql.SQLException {
      VexlioKits var0 = VexlioKits.getInstance();
      HashMap var1 = new HashMap();
      if (var0.config.useMySQL) {
         Connection var2 = var0.mysql.getConnection();
         PreparedStatement var3 = var2.prepareStatement("select * from " + var0.mysql.table);
         ResultSet var4 = var3.executeQuery();

         while(var4.next()) {
            String var5 = var4.getString("player_name");
            Player var6 = Bukkit.getPlayer(var5);
            var1.put(var5, var6 != null ? PlayerDataManager.get(var6).getStatisticsString((Player)null) : var4.getString("Statistics"));
         }

         var4.close();
         var3.close();
      } else {
         for(File var8 : getPlayersFiles()) {
            YamlConfiguration var9 = YamlConfiguration.loadConfiguration(var8);
            String var10 = var9.getString("Name");
            Player var11 = Bukkit.getPlayer(var10);
            var1.put(var10, var11 != null ? PlayerDataManager.get(var11).getStatisticsString((Player)null) : var9.getString("Statistics"));
         }
      }

      return var1;
   }

   public static List<File> getPlayersFiles() {
      ArrayList var0 = new ArrayList();
      File var1 = new File(VexlioKits.getInstance().getDataFolder(), "players");
      if (var1.exists() && var1.isDirectory()) {
         for(File var5 : var1.listFiles()) {
            if (var5.isFile()) {
               YamlConfiguration var6 = YamlConfiguration.loadConfiguration(var5);
               if (((FileConfiguration)var6).contains("Name") && ((FileConfiguration)var6).contains("Statistics")) {
                  var0.add(var5);
               }
            }
         }
      }

      return var0;
   }

   public static List<java.util.Map.Entry<String, Integer>> getTopPlayers(HashMap<String, String> var0, Stat var1, int var2) {
      if (var2 < 1) {
         throw new IllegalArgumentException("Amount must be a number above 0!");
      } else {
         HashMap var3 = new HashMap();

         for(String var5 : var0.keySet()) {
            String[] var6 = ((String)var0.get(var5)).split(":");
            if (var1.equals(VexlioKitsAPI.Stat.ELO) && var6.length < 13) {
               var3.put(var5, VexlioKits.getInstance().config.StartingELO);
            } else {
               var3.put(var5, Integer.valueOf(var6[var1.id]));
            }
         }

         if (var3.size() < var2) {
            int var7 = var2 - var3.size() + 1;

            for(int var9 = 1; var9 < var7; ++var9) {
               var3.put("NO_PLAYER" + var9, 0);
            }
         }

         LinkedList var8 = new LinkedList(var3.entrySet());
         Collections.sort(var8, new Comparator<java.util.Map.Entry<String, Integer>>() {
            public int compare(java.util.Map.Entry<String, Integer> var1, java.util.Map.Entry<String, Integer> var2) {
               return (Integer)var2.getValue() - (Integer)var1.getValue();
            }
         });
         return var8;
      }
   }

   public static PlayerData getPlayerData(Player var0) {
      return PlayerDataManager.get(var0);
   }

   public static enum Stat {
      KILLS(0),
      DEATHS(1),
      COINS(2),
      EXP(4),
      PROJECTILES_HIT(5),
      TOURNAMENT_WINS(6),
      CHALLENGE_WINS(7),
      ABILITIES_USED(8),
      SOUPS_EATEN(9),
      KILLSTREAKS_EARNED(10),
      ELO(12);

      int id;

      private Stat(int var3) {
         this.id = var3;
      }

      public static Stat getByName(String var0) {
         for(Stat var4 : values()) {
            if (var4.name().equals(var0)) {
               return var4;
            }
         }

         return null;
      }

      // $FF: synthetic method
      private static Stat[] $values() {
         return new Stat[]{KILLS, DEATHS, COINS, EXP, PROJECTILES_HIT, TOURNAMENT_WINS, CHALLENGE_WINS, ABILITIES_USED, SOUPS_EATEN, KILLSTREAKS_EARNED, ELO};
      }
   }
}
