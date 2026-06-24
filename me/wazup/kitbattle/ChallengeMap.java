package me.wazup.kitbattle;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import me.wazup.kitbattle.managers.PlayerDataManager;
import me.wazup.kitbattle.managers.SoundsManager;
import me.wazup.kitbattle.utils.Utils;
import me.wazup.kitbattle.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

public class ChallengeMap extends Map {
   public int playersPerTeam;
   BukkitTask grace;
   BukkitTask timer;
   BukkitTask finish;
   CustomScoreboard scoreboard;
   Team blueTeam;
   Team redTeam;
   public HashMap<UUID, Integer> players;
   boolean ranked;
   HashMap<UUID, Integer> playerGains;
   HashMap<UUID, Integer> playerLosses;

   public ChallengeMap(Kitbattle var1, String var2, List<Location> var3, int var4, boolean var5) {
      super(var1, var2, var3, var5);
      this.playersPerTeam = var4;
      this.scoreboard = new CustomScoreboard(var1, true, var1.msgs.challenge_scoreboard_title, var1.msgs.challengeScoreboard);
      this.scoreboard.updatePlaceholder("%team1_players%", 0);
      this.scoreboard.updatePlaceholder("%team2_players%", 0);
      this.scoreboard.updatePlaceholder("%playersperteam%", var4);
      this.scoreboard.updatePlaceholder("%map%", var2);
      this.blueTeam = this.registerTeam(ChatColor.BLUE);
      this.redTeam = this.registerTeam(ChatColor.RED);
      this.players = new HashMap();
   }

   private Team registerTeam(ChatColor var1) {
      Team var2 = this.scoreboard.registerTeam(var1.name());
      if (XMaterial.supports(16)) {
         var2.setColor(var1);
      } else {
         var2.setPrefix(var1.toString());
      }

      var2.setAllowFriendlyFire(false);
      return var2;
   }

   public boolean start(List<Player> var1, boolean var2) {
      for(Player var4 : var1) {
         if (var4.isDead()) {
            return false;
         }
      }

      for(Player var13 : var1) {
         this.players.put(var13.getUniqueId(), this.plugin.config.ChallengeLives);
         if (this.plugin.challengesManager != null) {
            this.plugin.challengesManager.players.add(var13.getUniqueId());
         }

         this.scoreboard.apply(var13);
         if (this.blueTeam.getSize() < this.playersPerTeam) {
            this.blueTeam.addPlayer(var13);
         } else {
            this.redTeam.addPlayer(var13);
         }

         this.plugin.spectating.remove(var13.getUniqueId());
         var13.closeInventory();
         this.plugin.clearData(var13);
         PlayerData var5 = PlayerDataManager.get(var13);
         if (this.plugin.config.challengeKitLock && this.plugin.Kits.containsKey(this.plugin.config.challengeKit.toLowerCase())) {
            Kit var6 = (Kit)this.plugin.Kits.get(this.plugin.config.challengeKit.toLowerCase());
            var5.setKit(var13, var6);
            var6.giveItems(var13);
         } else {
            this.plugin.giveDefaultItems(var13);
            var5.setKit(var13, (Kit)null);
         }

         var5.customScoreboard = null;
         var5.killstreak = 0;
         var5.deathstreak = 0;
         var5.damagers.clear();
         var13.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, this.plugin.config.challengesGraceLength * 20, 9999999));
         var13.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, this.plugin.config.challengesGraceLength * 20, 9999999));
         var13.setNoDamageTicks(this.plugin.config.challengesGraceLength * 20);
         if (this.plugin.tournamentsManager != null) {
            this.plugin.tournamentsManager.remove(var13, false);
         }

         if (this.plugin.challengesManager != null) {
            this.plugin.challengesManager.removeFromQueues(var13);
         }
      }

      String var12 = ((String)this.plugin.msgs.messages.get("Team-Color-Notification")).replace("%team%", ChatColor.BLUE + "BLUE");
      String var14 = ((String)this.plugin.msgs.messages.get("Team-Color-Notification")).replace("%team%", ChatColor.RED + "RED");
      Location var15 = (Location)this.spawnpoints.get(0);
      Location var16 = (Location)this.spawnpoints.get(1);

      for(OfflinePlayer var8 : this.blueTeam.getPlayers()) {
         ((Player)var8).sendMessage(var12);
         ((Player)var8).teleport(var15);
      }

      for(OfflinePlayer var19 : this.redTeam.getPlayers()) {
         ((Player)var19).sendMessage(var14);
         ((Player)var19).teleport(var16);
      }

      this.scoreboard.updatePlaceholder("%team1_players%", this.blueTeam.getSize());
      this.scoreboard.updatePlaceholder("%team2_players%", this.redTeam.getSize());
      this.ranked = var2;
      if (var2) {
         this.calculateELOS();

         for(Player var20 : var1) {
            for(String var10 : this.plugin.msgs.rankedMessages) {
               var20.sendMessage(var10.replace("%elo_gain%", String.valueOf(this.playerGains.get(var20.getUniqueId()))).replace("%elo_loss%", String.valueOf(this.playerLosses.get(var20.getUniqueId()))));
            }

            var20.playSound(var20.getLocation(), SoundsManager.WITHER_SPAWN, 1.0F, 1.0F);
         }
      }

      this.grace = (new BukkitRunnable() {
         int seconds;

         {
            this.seconds = ChallengeMap.this.plugin.config.challengesGraceLength;
         }

         public void run() {
            if (ChallengeMap.this.plugin.config.challengesGracePeriodWarnings.contains(this.seconds)) {
               String var1 = ((String)ChallengeMap.this.plugin.msgs.messages.get("Grace-Warning")).replace("%seconds%", String.valueOf(this.seconds));

               for(Player var3 : Utils.getPlayers(ChallengeMap.this.players.keySet())) {
                  var3.sendMessage(var1);
               }
            }

            --this.seconds;
            if (this.seconds == 0) {
               ChallengeMap.this.cancelTasks();
               ChallengeMap.this.startTimer();

               for(Player var5 : Utils.getPlayers(ChallengeMap.this.players.keySet())) {
                  var5.sendMessage((String)ChallengeMap.this.plugin.msgs.messages.get("Grace-End"));
                  var5.playSound(var5.getLocation(), SoundsManager.WITHER_SPAWN, 1.0F, 1.0F);
               }
            }

         }
      }).runTaskTimer(this.plugin, 0L, 20L);
      return true;
   }

   public void startTimer() {
      this.timer = (new BukkitRunnable() {
         public void run() {
            ChallengeMap.this.cancelTasks();
            ChallengeMap.this.timer = (new BukkitRunnable() {
               int seconds;

               {
                  this.seconds = ChallengeMap.this.plugin.config.challengesEndWarningsMax;
               }

               public void run() {
                  if (ChallengeMap.this.plugin.config.challengesEndWarnings.contains(this.seconds)) {
                     String var1 = ((String)ChallengeMap.this.plugin.msgs.messages.get("Challenge-End-Warning")).replace("%seconds%", String.valueOf(this.seconds));

                     for(Player var3 : Utils.getPlayers(ChallengeMap.this.players.keySet())) {
                        var3.sendMessage(var1);
                        var3.playSound(var3.getLocation(), SoundsManager.CLICK, 1.0F, 1.0F);
                     }
                  }

                  --this.seconds;
                  if (this.seconds == 0) {
                     ChallengeMap.this.stop();
                  }

               }
            }).runTaskTimer(ChallengeMap.this.plugin, 0L, 20L);
         }
      }).runTaskLater(this.plugin, (long)(this.plugin.config.maxChallengeTime * 20 - (this.plugin.config.challengesGracePeriodWarningsMax * 20 + this.plugin.config.challengesEndWarningsMax * 20)));
   }

   public void kill(final Player var1) {
      if ((Integer)this.players.get(var1.getUniqueId()) <= 1) {
         this.remove(var1, true);
      } else {
         this.players.put(var1.getUniqueId(), (Integer)this.players.get(var1.getUniqueId()) - 1);
         var1.sendMessage(((String)this.plugin.msgs.messages.get("Challenge-Player-Killed")).replace("%lives%", String.valueOf(this.players.get(var1.getUniqueId()))).replace("%seconds%", String.valueOf(this.plugin.config.ChallengeRespawnProtectionSeconds)));
         if (this.plugin.config.challengeKitLock && this.plugin.Kits.containsKey(this.plugin.config.challengeKit.toLowerCase())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
               public void run() {
                  Kit var1x = (Kit)ChallengeMap.this.plugin.Kits.get(ChallengeMap.this.plugin.config.challengeKit.toLowerCase());
                  var1x.giveItems(var1);
               }
            }, 3L);
         }
      }

   }

   public void remove(Player var1, boolean var2) {
      this.players.remove(var1.getUniqueId());
      PlayerData var3 = PlayerDataManager.get(var1);
      if (this.ranked) {
         var3.removeELO((Integer)this.playerLosses.get(var1.getUniqueId()));
      }

      if (this.plugin.challengesManager != null) {
         this.plugin.challengesManager.players.remove(var1.getUniqueId());
      }

      if (var3.getMap() == null) {
         this.plugin.leave(var1);
      } else {
         this.plugin.resetPlayerToMap(var1, var3.getMap(), false);
         var3.createScoreboard(var1);
      }

      String var4 = (var2 ? (String)this.plugin.msgs.messages.get("Challenge-Player-Eliminated") : (String)this.plugin.msgs.messages.get("Challenge-Player-Leave")).replace("%player%", (this.blueTeam.getPlayers().contains(var1) ? ChatColor.BLUE : ChatColor.RED) + var1.getName()).replace("%teamsize%", this.blueTeam.getPlayers().contains(var1) ? ChatColor.BLUE + String.valueOf(this.blueTeam.getSize() - 1) : ChatColor.RED + String.valueOf(this.redTeam.getSize() - 1)).replace("%maxteamsize%", String.valueOf(this.playersPerTeam));
      if (this.blueTeam.getPlayers().contains(var1)) {
         this.blueTeam.removePlayer(var1);
         this.scoreboard.updatePlaceholder("%team1_players%", this.blueTeam.getSize());
      } else {
         this.redTeam.removePlayer(var1);
         this.scoreboard.updatePlaceholder("%team2_players%", this.redTeam.getSize());
      }

      for(Player var6 : Utils.getPlayers(this.players.keySet())) {
         var6.sendMessage(var4);
      }

      if (this.blueTeam.getSize() == 0 || this.redTeam.getSize() == 0) {
         this.finish();
      }

   }

   public void finish() {
      this.cancelTasks();

      for(OfflinePlayer var2 : Utils.getPlayers(this.players.keySet())) {
         Player var3 = (Player)var2;
         var3.sendMessage((String)this.plugin.msgs.messages.get("Challenge-Player-Win"));
         var3.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, this.plugin.config.challengeCelebrationLength * 20, 9999999));
         var3.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, this.plugin.config.challengesGraceLength * 20, 9999999));

         for(String var5 : this.plugin.config.challengesWinnerCommands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var5.replace("%winner%", var3.getName()));
         }

         PlayerData var6 = PlayerDataManager.get(var3);
         var6.addChallengeWins(var3);
         if (this.ranked) {
            var6.addELO((Integer)this.playerGains.get(var3.getUniqueId()));
         }

         if (this.plugin.customMapsManager.isRegistered("Challenge-Winner-Map")) {
            var3.setItemInHand(this.plugin.customMapsManager.getItemStack("Challenge-Winner-Map"));
         }
      }

      this.finish = (new BukkitRunnable() {
         int seconds;
         boolean fireworks;

         {
            this.seconds = ChallengeMap.this.plugin.config.challengeCelebrationLength;
            this.fireworks = false;
         }

         public void run() {
            if (ChallengeMap.this.plugin.config.challengesFireworksCelebration) {
               this.fireworks = !this.fireworks;
               if (this.fireworks) {
                  for(Player var2 : Utils.getPlayers(ChallengeMap.this.players.keySet())) {
                     ChallengeMap.this.plugin.listen.spawnFirework(var2.getLocation());
                  }
               }
            }

            --this.seconds;
            if (this.seconds == 0) {
               ChallengeMap.this.stop();
            }

         }
      }).runTaskTimer(this.plugin, 0L, 20L);
   }

   public void stop() {
      List var1 = Utils.getPlayers(this.players.keySet());
      if (this.plugin.challengesManager != null) {
         this.plugin.challengesManager.players.removeAll(this.players.keySet());
      }

      this.players.clear();

      for(OfflinePlayer var3 : this.blueTeam.getPlayers()) {
         this.blueTeam.removePlayer(var3);
      }

      for(OfflinePlayer var8 : this.redTeam.getPlayers()) {
         this.redTeam.removePlayer(var8);
      }

      for(Player var9 : var1) {
         PlayerData var4 = PlayerDataManager.get(var9);
         if (var4.getMap() == null) {
            this.plugin.leave(var9);
         } else {
            this.plugin.resetPlayerToMap(var9, var4.getMap(), false);
            var4.createScoreboard(var9);
         }
      }

      this.cancelTasks();
      if (this.plugin.challengesManager != null) {
         boolean var7 = Utils.random.nextBoolean();
         this.plugin.challengesManager.checkQueue(this.playersPerTeam, var7);
         this.plugin.challengesManager.checkQueue(this.playersPerTeam, !var7);
      }

   }

   public void cancelTasks() {
      if (this.grace != null) {
         this.grace.cancel();
         this.grace = null;
      }

      if (this.timer != null) {
         this.timer.cancel();
         this.timer = null;
      }

      if (this.finish != null) {
         this.finish.cancel();
         this.finish = null;
      }

   }

   public boolean isAvailable() {
      return this.enabled && this.spawnpoints.size() > 1 && !this.isRunning();
   }

   public boolean isRunning() {
      return this.grace != null || this.timer != null || this.finish != null;
   }

   private void calculateELOS() {
      this.playerGains = new HashMap();
      this.playerLosses = new HashMap();
      int var1 = 0;

      for(OfflinePlayer var3 : this.blueTeam.getPlayers()) {
         var1 += PlayerDataManager.get(var3.getPlayer()).getELO();
      }

      int var8 = 0;

      for(OfflinePlayer var4 : this.redTeam.getPlayers()) {
         var8 += PlayerDataManager.get(var4.getPlayer()).getELO();
      }

      float var10 = (float)((double)1.0F / ((double)1.0F + Math.pow((double)10.0F, (double)((float)(var8 - var1) / 400.0F))));
      float var11 = 1.0F - var10;
      int var5 = this.plugin.config.EloChangeFactor;

      for(OfflinePlayer var7 : this.blueTeam.getPlayers()) {
         this.playerGains.put(var7.getUniqueId(), (int)((float)var5 * (1.0F - var10)));
         this.playerLosses.put(var7.getUniqueId(), (int)((float)var5 * var10));
      }

      for(OfflinePlayer var13 : this.redTeam.getPlayers()) {
         this.playerGains.put(var13.getUniqueId(), (int)((float)var5 * (1.0F - var11)));
         this.playerLosses.put(var13.getUniqueId(), (int)((float)var5 * var11));
      }

   }
}
