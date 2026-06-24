package me.wazup.kitbattle.holograms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.KitbattleAPI;
import me.wazup.kitbattle.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class GeneralizedHologramsManager {
   public Hologram leaderboardHologram;
   Location leaderboard;
   private HologramProvider provider;

   public GeneralizedHologramsManager(HologramProvider var1) {
      this.provider = var1;
   }

   public void setLeaderboardLocation(Location var1, boolean var2) {
      this.leaderboard = var1;
      if (var1 == null) {
         if (this.leaderboardHologram != null) {
            this.leaderboardHologram.delete();
            this.leaderboardHologram = null;
         }

      } else if (this.leaderboardHologram != null) {
         this.leaderboardHologram.teleport(var1);
      } else {
         this.leaderboardHologram = this.createHologram("KBLeaderboard", var1, Collections.singletonList(ChatColor.AQUA + "Loading..."));
         if (var2) {
            Kitbattle.getInstance().startLeaderboardUpdater();
         }

      }
   }

   private Hologram createHologram(String var1, Location var2, List<String> var3) {
      Object var4 = null;
      if (this.provider == GeneralizedHologramsManager.HologramProvider.DecentHolograms) {
         var4 = new DecentHologramsHologram(var1, var2, var3);
      } else if (this.provider == GeneralizedHologramsManager.HologramProvider.HolographicDisplays) {
         var4 = new HolographicDisplaysHologram(var2, var3);
      }

      return (Hologram)var4;
   }

   public void updateLeaderboards(HashMap<KitbattleAPI.Stat, Map.Entry<String, Integer>> var1) {
      ArrayList var2 = new ArrayList();
      if (!Messages.getInstance().holographicLeaderboardHeader.isEmpty()) {
         var2.add(Messages.getInstance().holographicLeaderboardHeader);
      }

      for(KitbattleAPI.Stat var4 : var1.keySet()) {
         Map.Entry var5 = (Map.Entry)var1.get(var4);
         var2.add(Messages.getInstance().holographicLeaderboardBody.replace("%stat%", var4.name()).replace("%player%", (CharSequence)var5.getKey()).replace("%score%", String.valueOf(var5.getValue())));
      }

      if (!Messages.getInstance().holographicLeaderboardFooter.isEmpty()) {
         var2.add(Messages.getInstance().holographicLeaderboardFooter);
      }

      this.leaderboardHologram.updateLines(var2);
   }

   public static enum HologramProvider {
      HolographicDisplays,
      DecentHolograms;

      // $FF: synthetic method
      private static HologramProvider[] $values() {
         return new HologramProvider[]{HolographicDisplays, DecentHolograms};
      }
   }
}
