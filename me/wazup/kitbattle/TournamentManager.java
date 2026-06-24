package me.wazup.kitbattle;

import java.util.ArrayList;
import java.util.UUID;
import me.wazup.kitbattle.managers.PlayerDataManager;
import me.wazup.kitbattle.managers.SoundsManager;
import me.wazup.kitbattle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TournamentManager {
   public TournamentMap map;
   private final Kitbattle plugin;
   private final ArrayList<UUID> players;
   private final ArrayList<UUID> queue;
   private BukkitTask countdown;
   private BukkitTask grace;
   private BukkitTask timer;
   private BukkitTask finishing;

   public TournamentManager(Kitbattle var1) {
      this.plugin = var1;
      this.players = new ArrayList();
      this.queue = new ArrayList();
   }

   public void add(Player var1) {
      if (this.isRunning() && !this.queue.contains(var1.getUniqueId())) {
         this.queue.add(var1.getUniqueId());
      } else {
         if (this.players.contains(var1.getUniqueId())) {
            return;
         }

         this.players.add(var1.getUniqueId());
         if (this.players.size() >= this.plugin.config.tournamentsMinPlayers && !this.isStarting()) {
            this.startCountdown();
         }
      }

   }

   private void startCountdown() {
      this.countdown = (new BukkitRunnable() {
         int seconds;

         {
            this.seconds = TournamentManager.this.plugin.config.tournamentsCountdownLength;
         }

         public void run() {
            if (TournamentManager.this.plugin.config.tournamentsTimeShownDuringCountdown.contains(this.seconds)) {
               String var1 = ((String)TournamentManager.this.plugin.msgs.messages.get("Tournament-Countdown")).replace("%seconds%", String.valueOf(this.seconds));

               for(Player var3 : Utils.getPlayers(TournamentManager.this.plugin.players)) {
                  var3.sendMessage(var1);
                  var3.playSound(var3.getLocation(), SoundsManager.CLICK, 1.0F, 1.0F);
               }
            }

            --this.seconds;
            if (this.seconds == 0) {
               TournamentManager.this.start();
            }

         }
      }).runTaskTimer(this.plugin, 0L, 20L);
   }

   private void start() {
      this.cancelTasks();
      ArrayList var1 = new ArrayList();

      for(TournamentMap var3 : this.plugin.tournamentMaps.values()) {
         if (var3.isAvailable()) {
            var1.add(var3);
         }
      }

      if (var1.isEmpty()) {
         for(Player var9 : Utils.getPlayers(this.players)) {
            var9.sendMessage((String)this.plugin.msgs.messages.get("Tournament-Cancel-Due-To-Maps"));
         }

         this.plugin.tournamentsManager = null;
      } else {
         this.map = (TournamentMap)var1.get(Utils.random.nextInt(var1.size()));
         String var5 = ((String)this.plugin.msgs.messages.get("Tournament-Start")).replace("%map%", this.map.name);

         for(Player var4 : Utils.getPlayers(this.plugin.players)) {
            var4.sendMessage(var5);
         }

         this.startGrace();

         for(Player var10 : Utils.getPlayers(this.players)) {
            if (this.plugin.challengesManager != null) {
               this.plugin.challengesManager.removeFromQueues(var10);
            }

            if (!var10.isDead()) {
               this.plugin.resetPlayerToMap(var10, this.map, false);
               var10.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, this.plugin.config.tournamentsGracePeriod * 20, 9999999));
               var10.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, this.plugin.config.tournamentsGracePeriod * 20, 9999999));
               var10.setNoDamageTicks(this.plugin.config.tournamentsGracePeriod * 20);
            } else {
               this.players.remove(var10.getUniqueId());
            }
         }

         if (this.players.size() < 2) {
            this.stop();
         }

      }
   }

   private void startGrace() {
      this.cancelTasks();
      this.grace = (new BukkitRunnable() {
         int seconds;

         {
            this.seconds = TournamentManager.this.plugin.config.tournamentsGracePeriod;
         }

         public void run() {
            if (TournamentManager.this.plugin.config.tournamentsGracePeriodWarnings.contains(this.seconds)) {
               String var1 = ((String)TournamentManager.this.plugin.msgs.messages.get("Grace-Warning")).replace("%seconds%", String.valueOf(this.seconds));

               for(Player var3 : Utils.getPlayers(TournamentManager.this.players)) {
                  var3.sendMessage(var1);
               }
            }

            --this.seconds;
            if (this.seconds == 0) {
               TournamentManager.this.cancelTasks();
               TournamentManager.this.startTimer();

               for(Player var5 : Utils.getPlayers(TournamentManager.this.players)) {
                  var5.sendMessage((String)TournamentManager.this.plugin.msgs.messages.get("Grace-End"));
                  var5.playSound(var5.getLocation(), SoundsManager.WITHER_SPAWN, 1.0F, 1.0F);
               }
            }

         }
      }).runTaskTimer(this.plugin, 0L, 20L);
   }

   public void startTimer() {
      this.timer = (new BukkitRunnable() {
         public void run() {
            TournamentManager.this.cancelTasks();
            TournamentManager.this.timer = (new BukkitRunnable() {
               int seconds;

               {
                  this.seconds = TournamentManager.this.plugin.config.tournamentsEndWarningsMax;
               }

               public void run() {
                  if (TournamentManager.this.plugin.config.tournamentsEndWarnings.contains(this.seconds)) {
                     String var1 = ((String)TournamentManager.this.plugin.msgs.messages.get("Tournament-End-Warning")).replace("%seconds%", String.valueOf(this.seconds));

                     for(Player var3 : Utils.getPlayers(TournamentManager.this.players)) {
                        var3.sendMessage(var1);
                        var3.playSound(var3.getLocation(), SoundsManager.CLICK, 1.0F, 1.0F);
                     }
                  }

                  --this.seconds;
                  if (this.seconds == 0) {
                     TournamentManager.this.stop();
                  }

               }
            }).runTaskTimer(TournamentManager.this.plugin, 0L, 20L);
         }
      }).runTaskLater(this.plugin, (long)(this.plugin.config.maxTournamentsTime * 20 - (this.plugin.config.tournamentsGracePeriodWarningsMax * 20 + this.plugin.config.tournamentsEndWarningsMax * 20)));
   }

   public void kill(Player var1) {
      this.players.remove(var1.getUniqueId());
      String var2 = ((String)this.plugin.msgs.messages.get("Tournament-Player-Eliminated")).replace("%player%", var1.getName()).replace("%remaining%", String.valueOf(this.players.size()));

      for(Player var4 : Utils.getPlayers(this.players)) {
         var4.sendMessage(var2);
      }

      this.checkFinish();
   }

   public void remove(Player var1, boolean var2) {
      if (this.queue.contains(var1.getUniqueId())) {
         this.queue.remove(var1.getUniqueId());
      } else if (this.players.contains(var1.getUniqueId())) {
         this.players.remove(var1.getUniqueId());
         if (this.isStarting()) {
            if (this.players.size() < this.plugin.config.tournamentsMinPlayers) {
               for(Player var4 : Utils.getPlayers(this.plugin.players)) {
                  var4.sendMessage((String)this.plugin.msgs.messages.get("Tournament-Cancel-Due-To-Players"));
                  var4.playSound(var1.getLocation(), SoundsManager.WITHER_SHOOT, 1.0F, 1.0F);
               }

               this.cancelTasks();
            }
         } else if (this.isRunning()) {
            PlayerData var7 = PlayerDataManager.get(var1);
            if (var7.getMap() == null) {
               this.plugin.leave(var1);
            } else {
               this.plugin.resetPlayerToMap(var1, var7.getMap(), false);
            }

            if (var2) {
               String var8 = ((String)this.plugin.msgs.messages.get("Tournament-Player-Leave")).replace("%player%", var1.getName()).replace("%remaining%", String.valueOf(this.players.size()));

               for(Player var6 : Utils.getPlayers(this.players)) {
                  var6.sendMessage(var8);
               }

               this.checkFinish();
            }
         }
      }

   }

   private void checkFinish() {
      if (this.players.size() < 2 && this.finishing == null) {
         this.finish();
      }

   }

   private void finish() {
      this.cancelTasks();
      if (this.players.size() == 1) {
         final Player var1 = Bukkit.getPlayer((UUID)this.players.get(0));
         if (var1 != null) {
            PlayerDataManager.get(var1).addTournamentWins(var1);
            var1.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, this.plugin.config.celebrationLength * 20, 9999999));
            var1.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, this.plugin.config.challengesGraceLength * 20, 9999999));
            String var2 = ((String)this.plugin.msgs.messages.get("Tournament-Player-Win")).replace("%player%", var1.getName());

            for(Player var4 : Utils.getPlayers(this.plugin.players)) {
               var4.sendMessage(var2);
            }

            for(String var6 : this.plugin.config.tournamentsWinnerCommands) {
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var6.replace("%winner%", var1.getName()));
            }

            this.finishing = (new BukkitRunnable() {
               int seconds;
               boolean fireworks;

               {
                  this.seconds = TournamentManager.this.plugin.config.celebrationLength;
                  this.fireworks = false;
               }

               public void run() {
                  if (TournamentManager.this.plugin.config.tournamentsFireworksCelebration && TournamentManager.this.players.contains(var1.getUniqueId())) {
                     this.fireworks = !this.fireworks;
                     if (this.fireworks) {
                        TournamentManager.this.plugin.listen.spawnFirework(var1.getLocation());
                     }
                  }

                  --this.seconds;
                  if (this.seconds == 0 || !TournamentManager.this.players.contains(var1.getUniqueId())) {
                     TournamentManager.this.stop();
                  }

               }
            }).runTaskTimer(this.plugin, 0L, 20L);
            if (this.plugin.customMapsManager.isRegistered("Tournament-Winner-Map")) {
               var1.setItemInHand(this.plugin.customMapsManager.getItemStack("Tournament-Winner-Map"));
            }
         }
      }

   }

   public void stop() {
      for(Player var2 : Utils.getPlayers(this.players)) {
         this.remove(var2, false);
      }

      this.cancelTasks();
      ArrayList var4 = new ArrayList();

      for(TournamentMap var3 : this.plugin.tournamentMaps.values()) {
         if (var3.isAvailable()) {
            var4.add(var3);
         }
      }

      if (!var4.isEmpty()) {
         this.map = null;
         this.applyQueue();
      } else {
         this.plugin.tournamentsManager = null;
      }

   }

   private void applyQueue() {
      for(Player var2 : Utils.getPlayers(this.queue)) {
         this.add(var2);
      }

      this.queue.clear();
   }

   public void clearQueue() {
      this.queue.clear();
   }

   public boolean contains(Player var1) {
      return this.players.contains(var1.getUniqueId());
   }

   public boolean isQueueing(Player var1) {
      return this.queue.contains(var1.getName());
   }

   public boolean isStarting() {
      return this.countdown != null;
   }

   public boolean isRunning() {
      return this.grace != null || this.timer != null || this.finishing != null;
   }

   public int getSize() {
      return this.players.size();
   }

   public void cancelTasks() {
      if (this.countdown != null) {
         this.countdown.cancel();
         this.countdown = null;
      }

      if (this.grace != null) {
         this.grace.cancel();
         this.grace = null;
      }

      if (this.timer != null) {
         this.timer.cancel();
         this.timer = null;
      }

      if (this.finishing != null) {
         this.finishing.cancel();
         this.finishing = null;
      }

   }
}
