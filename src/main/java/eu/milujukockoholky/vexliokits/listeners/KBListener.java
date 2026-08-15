package eu.milujukockoholky.vexliokits.listeners;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import eu.milujukockoholky.vexliokits.ChallengeMap;
import eu.milujukockoholky.vexliokits.Config;
import eu.milujukockoholky.vexliokits.Kit;
import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.VexlioKitsAPI;
import eu.milujukockoholky.vexliokits.PlayerData;
import eu.milujukockoholky.vexliokits.PlayingMap;
import eu.milujukockoholky.vexliokits.Rank;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
import eu.milujukockoholky.vexliokits.managers.SoundsManager;
import eu.milujukockoholky.vexliokits.managers.TitleManager;
import eu.milujukockoholky.vexliokits.utils.Cuboid;
import eu.milujukockoholky.vexliokits.utils.ItemStackBuilder;
import eu.milujukockoholky.vexliokits.utils.Utils;
import eu.milujukockoholky.vexliokits.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class KBListener implements Listener {
   public ItemStack soup;
   ItemStack air;
   ItemStack potion;
   HashMap<String, String> privateDuels;
   private final VexlioKits plugin;
   Material farmLand;

   public KBListener() {
      this.soup = XMaterial.MUSHROOM_STEW.parseItem();
      this.air = new ItemStack(Material.AIR);
      this.privateDuels = new HashMap();
      this.farmLand = XMaterial.FARMLAND.parseMaterial();
      this.plugin = VexlioKits.getInstance();
      if (Material.getMaterial("SPLASH_POTION") != null) {
         ItemStackBuilder var1 = new ItemStackBuilder(Material.getMaterial("SPLASH_POTION"));
         var1.setPotionEffect(PotionType.INSTANT_HEAL, false, true);
         this.potion = var1.build();
      } else {
         this.potion = (new ItemStackBuilder(Material.POTION)).setDurability(16421).build();
      }

   }

   @EventHandler
   public void onPlayerBreakBlock(BlockBreakEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.plugin.players.contains(var2.getUniqueId()) && !this.plugin.editmode.contains(var2.getUniqueId())) {
         var1.setCancelled(true);
      }

      if (!var1.isCancelled()) {
         if (var1.getBlock().getState() instanceof Sign) {
            Sign var3 = (Sign)var1.getBlock().getState();
            if (var3.getLine(0).equals(this.plugin.config.SignsPrefix) || var3.getLine(0).startsWith(ChatColor.AQUA + "Top #" + ChatColor.RED)) {
               if (!var2.hasPermission("VexlioKits.breaksigns")) {
                  var2.sendMessage((String)this.plugin.msgs.messages.get("No-Permission"));
                  var1.setCancelled(true);
                  return;
               }

               if (this.plugin.topSigns.containsKey(var3.getLocation())) {
                  int var4 = (Integer)this.plugin.topSigns.get(var3.getLocation());
                  this.plugin.fileManager.getConfig("signs.yml").set("Signs.Top." + var4, (Object)null);
                  this.plugin.fileManager.saveConfig("signs.yml");
                  this.plugin.topSigns.remove(var3.getLocation());
                  if (this.plugin.topSigns.isEmpty() && this.plugin.leaderboard_updater != null) {
                     this.plugin.leaderboard_updater.cancel();
                     this.plugin.leaderboard_updater = null;
                  }

                  var2.sendMessage(this.plugin.msgs.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GOLD + " removed the top sign with the id of " + ChatColor.LIGHT_PURPLE + "#" + var4);
               }

               if (var3.getLine(1).equals(this.plugin.config.JoinPrefix)) {
                  String var8 = ChatColor.stripColor(var3.getLine(2).toLowerCase());
                  if (this.plugin.playingMaps.containsKey(var8)) {
                     PlayingMap var5 = (PlayingMap)this.plugin.playingMaps.get(var8);
                     if (var5.signs.containsKey(var3.getLocation())) {
                        int var6 = (Integer)var5.signs.get(var3.getLocation());
                        FileConfiguration var7 = this.plugin.fileManager.getConfig("maps.yml");
                        var7.set("Maps." + var5.name + ".Signs." + var6, (Object)null);
                        this.plugin.fileManager.saveConfig("maps.yml");
                        var5.signs.remove(var3.getLocation());
                        var2.sendMessage(this.plugin.msgs.prefix + "You have " + ChatColor.RED + "removed" + ChatColor.GRAY + " the join sign with the id #" + ChatColor.LIGHT_PURPLE + var6);
                     }
                  }
               }
            }
         }

      }
   }

   @EventHandler
   public void onPlayerBlockPlace(BlockPlaceEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.plugin.players.contains(var2.getUniqueId()) && !this.plugin.editmode.contains(var2.getUniqueId())) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerLogin(PlayerLoginEvent var1) {
      if (this.plugin.config.bungeeMode && (this.plugin.bungeeMode == null || this.plugin.bungeeMode.getMap() == null)) {
         var1.disallow(Result.KICK_OTHER, (String)this.plugin.msgs.messages.get("No-Available-Maps"));
      }

   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent var1) {
      Player var2 = var1.getPlayer();
      PlayerDataManager.load(var2);
      if (this.plugin.getConfig().getBoolean("Check-For-Updates") && var2.hasPermission("VexlioKits.admin")) {
         var2.sendMessage(this.plugin.msgs.prefix + (this.plugin.availableUpdate ? "Found a new available version! " + ChatColor.LIGHT_PURPLE + "download at https://goo.gl/acFc6M" : "Looks like you have the latest version installed!"));
      }

      if (this.plugin.bungeeMode != null) {
         this.plugin.join(var2, this.plugin.bungeeMode.getMap(), 10);
      }

   }

   @EventHandler
   public void onPlayerLeave(PlayerQuitEvent var1) {
      Player var2 = var1.getPlayer();
      PlayerData var3 = PlayerDataManager.get(var2);
      var3.saveStatsIntoFile(var2, false);
      var3.cancelBounty(var2.getName());
      if (this.plugin.players.contains(var2.getUniqueId())) {
         this.plugin.leave(var2);
         if (!var3.damagers.isEmpty() && (System.currentTimeMillis() - var3.lastHitTime) / 1000L <= (long)this.plugin.config.combatLogDuration) {
            var3.addDeaths();
            this.awardDamagers(var2, var3);
            Player var4 = null;
            if (!var3.lastHit.isEmpty()) {
               var4 = Bukkit.getPlayer(var3.lastHit);
               if (var4 != null && !var4.getName().equals(var2.getName()) && this.plugin.players.contains(var4.getUniqueId())) {
                  this.awardKiller(var4, var2, var3);
               }
            }

            if (this.plugin.config.SendDeathMessageToEveryone) {
               String var5 = this.getDeathMessage(var2, var4);

               for(Player var7 : Utils.getPlayers(this.plugin.players)) {
                  var7.sendMessage(var5);
               }
            }
         }
      }

      PlayerDataManager.remove(var2);
   }

   @EventHandler
   public void onPlayerRespawn(PlayerRespawnEvent var1) {
      Player var2 = var1.getPlayer();
      PlayerData var3 = PlayerDataManager.get(var2);
      if (this.plugin.players.contains(var2.getUniqueId()) && var3.getMap() != null) {
         var1.setRespawnLocation(var3.getMap().getSpawnpoint());
      }

   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent var1) {
      final Player var2 = var1.getEntity();
      if (this.plugin.players.contains(var2.getUniqueId())) {
         if (!this.plugin.config.respawnScreenOnDeath) {
            if (XMaterial.supports(8)) {
               (new BukkitRunnable() {
                  public void run() {
                     var2.spigot().respawn();
                  }
               }).runTaskLater(VexlioKits.getInstance(), 1L);
            } else {
               var2.setHealth(var2.getMaxHealth());
               if (var2.getVehicle() != null) {
                  var2.getVehicle().eject();
               }

               var2.setFallDistance(0.0F);
            }
         }

         var2.closeInventory();
         if (!this.plugin.config.DoPlayersDropItemsOnDeath) {
            var1.getDrops().clear();
         }

var1.setDeathMessage((String)null);
          final PlayerData var3 = PlayerDataManager.get(var2);
          var3.addDeaths();
          this.plugin.minigameProvider.recordMetric(var2.getUniqueId(), var2.getName(), "deaths", 1);
          this.plugin.minigameProvider.sendPlayerLost(var2.getUniqueId());
         var3.setKit(var2, (Kit)null);
         var3.killstreak = 0;
         ++var3.deathstreak;
         if (var3.customScoreboard != null) {
            var3.customScoreboard.updatePlaceholder("%deaths%", var3.getDeaths());
            var3.customScoreboard.updatePlaceholder("%deathstreak%", var3.getDeathstreak());
            var3.customScoreboard.updatePlaceholder("%killstreak%", var3.getKillstreak());
         }

         if (this.plugin.isInTournament(var2)) {
            this.plugin.tournamentsManager.kill(var2);
         } else if (this.plugin.isInChallenge(var2)) {
            for(ChallengeMap var5 : this.plugin.challengeMaps.values()) {
               if (var5.players.containsKey(var2.getUniqueId())) {
                  var5.kill(var2);
                  break;
               }
            }
         }

         if (!var3.damagers.isEmpty()) {
            this.awardDamagers(var2, var3);
         }

         Player var8 = var2.getKiller() != null && PlayerDataManager.get(var2.getKiller()) != null ? var2.getKiller() : null;
         if (this.plugin.config.SendDeathMessageToEveryone) {
            String var9 = this.getDeathMessage(var2, var8);

            for(Player var7 : Utils.getPlayers(this.plugin.players)) {
               var7.sendMessage(var9);
            }
         }

         if (this.plugin.config.Deathstreaks.containsKey(var3.deathstreak)) {
            String var10 = ((String)this.plugin.msgs.messages.get("Deathstreak-Receive")).replace("%player%", var2.getName()).replace("%player%", var2.getName()).replace("%deathstreak%", String.valueOf(var3.deathstreak));

            for(Player var12 : Utils.getPlayers(this.plugin.players)) {
               var12.sendMessage(var10);
            }
         }

         if (var8 != null) {
            if (!var8.getName().equals(var2.getName())) {
               if (!TitleManager.getInstance().sendTitle(var2, ((String)this.plugin.msgs.titles.get("Death")).replace("%killer%", var8.getName()))) {
                  var2.sendMessage(((String)this.plugin.msgs.messages.get("Player-Death-Message")).replace("%killer%", var8.getName()));
               }

               this.awardKiller(var8, var2, var3);
            } else {
               var2.sendMessage((String)this.plugin.msgs.messages.get("Player-Suicide-Message"));
            }
         }

         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            public void run() {
               if (KBListener.this.plugin.players.contains(var2.getUniqueId())) {
                  KBListener.this.plugin.giveDefaultItems(var2);
                  if (!KBListener.this.plugin.config.respawnScreenOnDeath) {
                     var2.teleport(var3.getMap().getSpawnpoint());
                     var2.setFireTicks(0);
                     var2.setLevel(0);
                     var2.setExp(0.0F);
                     var2.setFoodLevel(20);

                     for(PotionEffect var2x : var2.getActivePotionEffects()) {
                        var2.removePotionEffect(var2x.getType());
                     }

                     for(PotionEffect var5 : Config.getInstance().respawnEffects) {
                        var2.addPotionEffect(var5);
                     }

                     if (KBListener.this.plugin.config.OpenKitsMenuOnRespawn) {
                        var3.kitsInventory.open(var2);
                     }
                  }

                  if (KBListener.this.plugin.isInChallenge(var2)) {
                     var2.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, KBListener.this.plugin.config.ChallengeRespawnProtectionSeconds * 20, 100));
                     var2.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, KBListener.this.plugin.config.ChallengeRespawnProtectionSeconds * 20, 100));
                     var2.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, KBListener.this.plugin.config.ChallengeRespawnProtectionSeconds * 20, 100));

                     for(ChallengeMap var6 : KBListener.this.plugin.challengeMaps.values()) {
                        if (var6.players.containsKey(var2.getUniqueId())) {
                           var2.teleport(var6.getSpawnpoint());
                           break;
                        }
                     }
                  }

                  var2.setAllowFlight(false);
                  var2.setFlying(false);
               }
            }
         }, 2L);
      }

   }

   private void awardDamagers(Player var1, PlayerData var2) {
      if (this.plugin.config.KillCoinsContribution) {
         double var3 = (double)0.0F;

         for(double var6 : var2.damagers.values()) {
            var3 += var6;
         }

         for(String var15 : var2.damagers.keySet()) {
            Player var7 = Bukkit.getPlayer(var15);
            if (var7 != null) {
               double var8 = Double.valueOf((Double)var2.damagers.get(var15) / var3);
               double var10 = this.plugin.getModifier(var7);
               int var12 = (int)((double)this.plugin.config.EarnedCoinsPerKill * var8 * var10);
               if (var12 != 0) {
                  PlayerData var13 = PlayerDataManager.get(var7);
                  var13.addCoins(var7, var12);
                  var7.sendMessage(((String)this.plugin.msgs.messages.get("Player-Kill")).replace("%percentage%", String.valueOf((int)(var8 * (double)100.0F))).replace("%player%", var1.getName()).replace("%coins%", String.valueOf(var12)) + (var10 > (double)1.0F ? ChatColor.GRAY + " (" + ChatColor.AQUA + "x" + var10 + ChatColor.GRAY + ")!" : ""));
                  if (var13.customScoreboard != null) {
                     var13.customScoreboard.updatePlaceholder("%coins%", var13.getCoins(var7));
                  }
               }
            }
         }
      }

      var2.damagers.clear();
   }

   private void awardKiller(Player var1, Player var2, PlayerData var3) {
      PlayerData var4 = PlayerDataManager.get(var1);
      var4.addKills(var1);
      this.plugin.minigameProvider.sendPlayerKilled(var2.getUniqueId(), var1.getUniqueId());
      this.plugin.minigameProvider.recordMetric(var1.getUniqueId(), var1.getName(), "kills", 1);
      this.plugin.minigameProvider.claimReward(var1.getUniqueId(), var1.getName(), "kill_reward", (long)this.plugin.config.EarnedCoinsPerKill * 100L);
      ++var4.killstreak;
      if (var4.deathstreak >= this.plugin.config.leastDeathstreak) {
         for(String var6 : this.plugin.config.DeathstreakEndCommands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var6.replace("%player%", var1.getName()));
         }

         String var12 = ((String)this.plugin.msgs.messages.get("Deathstreak-End")).replace("%player%", var1.getName()).replace("%player%", var1.getName());

         for(Player var7 : Utils.getPlayers(this.plugin.players)) {
            var7.sendMessage(var12);
         }
      }

      var4.deathstreak = 0;
      var1.setLevel(var4.killstreak);
      if (this.plugin.config.Killstreaks.containsKey(var4.killstreak)) {
         TitleManager.getInstance().sendActionBar(var1, ((String)this.plugin.msgs.titles.get("Killstreak")).replace("%kills%", String.valueOf(var4.killstreak)));
         if (this.plugin.config.SendKillstreaksToEveryone) {
            String var13 = ((String)this.plugin.msgs.messages.get("Player-Get-Killstreak-Announcement")).replace("%player%", var1.getName()).replace("%kills%", String.valueOf(var4.killstreak));

            for(Player var19 : Utils.getPlayers(this.plugin.players)) {
               var19.sendMessage(var13);
            }
         } else {
            var1.sendMessage(((String)this.plugin.msgs.messages.get("Player-Get-Killstreak-Self-Message")).replace("%kills%", String.valueOf(var4.killstreak)));
         }

         for(String var18 : (List<String>)this.plugin.config.Killstreaks.get(var4.killstreak)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var18.replace("%player%", var1.getName()));
         }

         var4.addKillstreaksEarned(var1);
      }

      double var15 = this.plugin.getModifier(var1);
      int var20 = (int)((double)(Integer)this.plugin.config.possibleExp.get(Utils.random.nextInt(this.plugin.config.possibleExp.size())) * var15);
      if (!this.plugin.config.KillCoinsContribution) {
         int var8 = (int)((double)this.plugin.config.EarnedCoinsPerKill * var15);
         var4.addCoins(var1, var8);
         var1.sendMessage(((String)this.plugin.msgs.messages.get("Player-Kill")).replace("%player%", var2.getName()).replace("%coins%", String.valueOf(var8)).replace("%exp%", String.valueOf(var20)) + (var15 > (double)1.0F ? ChatColor.GRAY + " (" + ChatColor.AQUA + "x" + var15 + ChatColor.GRAY + ")!" : ""));
      }

      int var21 = var3.getTotalBounty();
      if (var21 > 0) {
         var4.addCoins(var1, var21);
         if (var4.customScoreboard != null) {
            var4.customScoreboard.updatePlaceholder("%coins%", var4.getCoins(var1));
         }

         var3.bounties.clear();
         String var9 = ((String)this.plugin.msgs.messages.get("Bounty-Claim")).replace("%killer%", var1.getName()).replace("%player%", var2.getName()).replace("%bounty%", String.valueOf(var21));

         for(Player var11 : Utils.getPlayers(this.plugin.players)) {
            var11.sendMessage(var9);
         }
      }

      boolean var22 = var4.addExp(var1, var20);
      TitleManager.getInstance().sendTitle(var1, ((String)this.plugin.msgs.titles.get("Earn-Exp")).replace("%exp%", String.valueOf(var20)));
      if (var4.customScoreboard != null) {
         var4.customScoreboard.updatePlaceholder("%kills%", var4.getKills());
         var4.customScoreboard.updatePlaceholder("%killstreak%", var4.getKillstreak());
         var4.customScoreboard.updatePlaceholder("%deathstreak%", var4.getDeathstreak());
         var4.customScoreboard.updatePlaceholder("%player_exp%", var4.getExp());
         if (!this.plugin.config.KillCoinsContribution) {
            var4.customScoreboard.updatePlaceholder("%coins%", var4.getCoins(var1));
         }

         if (var4.getNextRank() != null) {
            var4.customScoreboard.updatePlaceholder("%player_next_rank_exp%", var4.getNextRank().getRequiredExp());
            var4.customScoreboard.updatePlaceholder("%player_next_rank_exp_difference%", var4.getNextRank().getRequiredExp() - var4.getExp());
         }

         if (var22) {
            var4.customScoreboard.updatePlaceholder("%player_rank%", var4.getRank().getName());
            var4.customScoreboard.updatePlaceholder("%player_next_rank%", var4.getNextRank() != null ? var4.getNextRank().getName() : "None");
         }
      }

   }

   @EventHandler
   public void onPlayerPickupItem(PlayerPickupItemEvent var1) {
      if (this.plugin.players.contains(var1.getPlayer().getUniqueId()) && !this.plugin.config.CanPlayersPickItemsOnGround) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerDropItem(final PlayerDropItemEvent var1) {
      if (this.plugin.players.contains(var1.getPlayer().getUniqueId()) && !this.plugin.config.CanPlayersDropItemsOnGround) {
         if (var1.getItemDrop().getItemStack().getType().equals(Material.BOWL)) {
            if (this.plugin.config.SoupDropSound) {
               var1.getPlayer().playSound(var1.getPlayer().getLocation(), SoundsManager.ITEM_PICKUP, 1.0F, 1.0F);
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
               public void run() {
                  var1.getItemDrop().remove();
               }
            }, 20L);
         } else {
            var1.setCancelled(true);
         }
      }

   }

   @EventHandler
   public void onFoodLevelChange(FoodLevelChangeEvent var1) {
      if (this.plugin.players.contains(var1.getEntity().getUniqueId()) && !this.plugin.config.DoPlayersLoseHunger) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent var1) {
      final Player var2 = var1.getPlayer();
      ItemStack var3 = var2.getItemInHand();
      if (this.plugin.selectionMode.containsKey(var2.getUniqueId()) && Utils.compareItem(var3, this.plugin.wand_itemstack)) {
         var1.setCancelled(true);
         int var11 = var1.getAction().equals(Action.LEFT_CLICK_BLOCK) ? 0 : (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK) ? 1 : 2);
         if (var11 != 2) {
            Location var15 = var1.getClickedBlock().getLocation();
            Location var21 = ((Location[])this.plugin.selectionMode.get(var2.getUniqueId()))[var11 == 1 ? 0 : 1];
            ((Location[])this.plugin.selectionMode.get(var2.getUniqueId()))[var11] = var15;
            if (var21 != null && !var21.getWorld().getName().equals(var15.getWorld().getName())) {
               var21 = null;
            }

            var2.sendMessage(this.plugin.msgs.prefix + "You have set the " + ChatColor.LIGHT_PURPLE + "#" + (var11 + 1) + ChatColor.GRAY + " corner at " + Utils.getReadableLocationString(var15, false) + (var21 != null ? ChatColor.AQUA + " (" + (new Cuboid(var15, var21)).getSize() + ")" : ""));
         }
      } else {
         if (var1.getAction().equals(Action.RIGHT_CLICK_BLOCK) && var1.getClickedBlock().getState() instanceof Sign) {
            Sign var4 = (Sign)var1.getClickedBlock().getState();
            if (var4.getLine(0).equals(this.plugin.config.SignsPrefix)) {
               var1.setCancelled(true);
               if (var4.getLine(1).equals(this.plugin.config.JoinPrefix)) {
                  if (var2.getItemInHand().getType() != Material.AIR) {
                     var2.sendMessage((String)this.plugin.msgs.messages.get("Join-Denied"));
                     return;
                  }

                  var2.performCommand("VexlioKits join " + ChatColor.stripColor(var4.getLine(2)));
               } else if (var4.getLine(1).equals(this.plugin.config.LeavePrefix)) {
                  if (this.plugin.players.contains(var2.getUniqueId())) {
                     var2.performCommand("VexlioKits leave");
                     var2.updateInventory();
                  } else {
                     var2.sendMessage((String)this.plugin.msgs.messages.get("Not-In-A-Game"));
                  }
               } else if (var4.getLine(1).equals(this.plugin.config.StatsPrefix)) {
                  var2.performCommand("VexlioKits stats");
               } else if (var4.getLine(1).equals(this.plugin.config.SoupPrefix)) {
                  if (this.plugin.players.contains(var2.getUniqueId())) {
                     PlayerData var12 = PlayerDataManager.get(var2);
                     if (var12.hasCooldown(var2, "SoupSignCooldown")) {
                        return;
                     }

                     var12.setCooldown(var2, "SoupSignCooldown", Config.getInstance().SoupSignCooldown, false);
                     if (!var4.getLine(3).isEmpty() && Utils.checkNumbers(var4.getLine(3))) {
                        int var17 = Integer.parseInt(var4.getLine(3));
                        int var24 = var12.getCoins(var2);
                        if (var24 < var17) {
                           var2.sendMessage((String)this.plugin.msgs.messages.get("Not-Enough-Coins"));
                           return;
                        }

                        var12.removeCoins(var2, var17);
                        var2.sendMessage(((String)this.plugin.msgs.messages.get("Player-Stat-Modification-Through-Command")).replace("%amount%", String.valueOf(var24 - var17)).replace("%stat%", "coins"));
                        if (var12.customScoreboard != null) {
                           var12.customScoreboard.updatePlaceholder("%coins%", var12.getCoins(var2));
                        }
                     }

                     Inventory var18 = Bukkit.createInventory(var2, 36, "Soup");

                     for(int var25 = 0; var25 < 36; ++var25) {
                        var18.addItem(new ItemStack[]{this.soup});
                     }

                     var2.openInventory(var18);
                  } else {
                     var2.sendMessage((String)this.plugin.msgs.messages.get("Not-In-A-Game"));
                  }
               } else if (var4.getLine(1).equals(this.plugin.config.PotionsPrefix)) {
                  if (this.plugin.players.contains(var2.getUniqueId())) {
                     if (!var4.getLine(3).isEmpty() && Utils.checkNumbers(var4.getLine(3))) {
                        int var13 = Integer.valueOf(var4.getLine(3));
                        PlayerData var19 = PlayerDataManager.get(var2);
                        int var26 = var19.getCoins(var2);
                        if (var26 < var13) {
                           var2.sendMessage((String)this.plugin.msgs.messages.get("Not-Enough-Coins"));
                           return;
                        }

                        var19.removeCoins(var2, var13);
                        var2.sendMessage(((String)this.plugin.msgs.messages.get("Player-Stat-Modification-Through-Command")).replace("%amount%", String.valueOf(var26 - var13)).replace("%stat%", "coins"));
                        if (var19.customScoreboard != null) {
                           var19.customScoreboard.updatePlaceholder("%coins%", var19.getCoins(var2));
                        }
                     }

                     Inventory var14 = Bukkit.createInventory(var2, 36, "Potions");

                     for(int var20 = 0; var20 < 36; ++var20) {
                        var14.addItem(new ItemStack[]{this.potion});
                     }

                     var2.openInventory(var14);
                  } else {
                     var2.sendMessage((String)this.plugin.msgs.messages.get("Not-In-A-Game"));
                  }
               }

               return;
            }
         }

         if (this.plugin.players.contains(var2.getUniqueId())) {
            if (!var1.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !var1.getAction().equals(Action.RIGHT_CLICK_AIR)) {
               if (var1.getAction().equals(Action.PHYSICAL) && var1.getClickedBlock().getType().equals(this.farmLand)) {
                  var1.setCancelled(true);
               }
            } else {
               if (var3.getType().equals(this.soup.getType())) {
                  if (var2.getHealth() == var2.getMaxHealth()) {
                     return;
                  }

                  var2.setHealth(Math.min(var2.getHealth() + (double)7.0F, var2.getMaxHealth()));
                  var2.getItemInHand().setType(Material.BOWL);
                  if (this.plugin.config.SoupAutoDisappear) {
                     (new BukkitRunnable() {
                        public void run() {
                           if (var2.getItemInHand().getType().equals(Material.BOWL)) {
                              var2.setItemInHand(KBListener.this.air);
                           }

                        }
                     }).runTaskLater(this.plugin, 1L);
                  }

                  PlayerDataManager.get(var2).addSoupsEaten(var2);
                  return;
               }

               if (PlayerDataManager.get(var2).getKit() == null) {
                  if (Utils.compareItem(var3, ((Config.HotbarItem)VexlioKits.getInstance().config.hotBarItems.get("Kit-Selector")).item)) {
                     PlayerDataManager.get(var2).kitsInventory.open(var2);
                     return;
                  }

                  if (Utils.compareItem(var3, ((Config.HotbarItem)VexlioKits.getInstance().config.hotBarItems.get("Profile-Item")).item)) {
                     if (this.plugin.achievementsManager.enabled) {
                        var2.openInventory(this.plugin.profileInventory);
                     } else {
                        var2.openInventory(PlayerDataManager.get(var2).getStatsInventory(var2));
                     }

                     return;
                  }

                  if (Utils.compareItem(var3, ((Config.HotbarItem)VexlioKits.getInstance().config.hotBarItems.get("Shop-Opener")).item)) {
                     this.plugin.shop.open(var2);
                     return;
                  }

                  if (Utils.compareItem(var3, ((Config.HotbarItem)VexlioKits.getInstance().config.hotBarItems.get("Trails-Opener")).item)) {
                     if (this.plugin.trailsInventory != null && var2.hasPermission("VexlioKits.trails")) {
                        var2.openInventory(this.plugin.trailsInventory);
                     } else {
                        var2.sendMessage((String)this.plugin.msgs.messages.get("No-Permission"));
                     }

                     return;
                  }

                  if (XMaterial.GRAY_DYE.isSimilar(var3) || XMaterial.LIME_DYE.isSimilar(var3)) {
                     if (this.plugin.tournamentsManager != null) {
                        PlayerData var10 = PlayerDataManager.get(var2);
                        if (var10.hasCooldown(var2, "Tournament")) {
                           return;
                        }

                        var10.setCooldown(var2, "Tournament", 2, false);
                        if (!this.plugin.tournamentsManager.isQueueing(var2) && !this.plugin.tournamentsManager.contains(var2)) {
                           this.plugin.tournamentsManager.add(var2);
                           var2.setItemInHand((new ItemStackBuilder(XMaterial.LIME_DYE.parseItem())).setName(ChatColor.AQUA + "Tournament: " + ChatColor.GREEN + "Enabled").build());
                           var2.playSound(var2.getLocation(), SoundsManager.CLICK, 1.0F, 1.0F);
                        } else {
                           this.plugin.tournamentsManager.remove(var2, false);
                           var2.setItemInHand((new ItemStackBuilder(XMaterial.GRAY_DYE.parseItem())).setName(ChatColor.AQUA + "Tournament: " + ChatColor.RED + "Disabled").build());
                           var2.playSound(var2.getLocation(), SoundsManager.CLICK, 1.0F, 1.0F);
                        }
                     } else {
                        var2.setItemInHand(new ItemStack(Material.AIR));
                     }

                     return;
                  }

                  if (Utils.compareItem(var3, ((Config.HotbarItem)VexlioKits.getInstance().config.hotBarItems.get("Challenges-Item")).item)) {
                     if (this.plugin.challengesManager != null) {
                        this.plugin.challengesManager.openMenu(var2);
                        var2.playSound(var2.getLocation(), SoundsManager.CLICK, 1.0F, 1.0F);
                     } else {
                        var2.setItemInHand(new ItemStack(Material.AIR));
                     }

                     return;
                  }

                  if (Utils.compareItem(var2.getItemInHand(), ((Config.HotbarItem)VexlioKits.getInstance().config.hotBarItems.get("Kit-Unlocker")).item)) {
                     final PlayerData var9 = PlayerDataManager.get(var2);
                     if (var9.hasCooldown(var2, "Kitunlocker")) {
                        return;
                     }

                     var9.setCooldown(var2, "Kitunlocker", 5, false);
                     final ArrayList var5 = new ArrayList();

                     for(ItemStack var7 : this.plugin.shop.getAllContents()) {
                        Kit var8 = (Kit)this.plugin.Kits.get(ChatColor.stripColor(var7.getItemMeta().getDisplayName().toLowerCase()));
                        if (var8.isEnabled() && (!var8.requirePermission || var2.hasPermission(var8.permission))) {
                           var5.add(var7);
                        }
                     }

                     if (var5.isEmpty()) {
                        var2.sendMessage((String)this.plugin.msgs.messages.get("Already-Unlocked-All-Kits"));
                        return;
                     }

                     if (var2.getItemInHand().getAmount() > 1) {
                        var2.getItemInHand().setAmount(var2.getItemInHand().getAmount() - 1);
                     } else {
                        var2.setItemInHand(new ItemStack(Material.AIR));
                        var1.setCancelled(true);
                     }

                     --var9.kitUnlockers;
                     var2.sendMessage((String)this.plugin.msgs.messages.get("Player-Open-KitUnlocker"));
                     final Inventory var16 = Bukkit.createInventory(var2, 54, (String)this.plugin.msgs.inventories.get("Kit-Unlocker"));
                     var2.openInventory(var16);
                     var2.playSound(var2.getLocation(), SoundsManager.ITEM_PICKUP, 1.0F, 1.0F);

                     for(int var22 = 0; var22 < 3; ++var22) {
                        var2.playEffect(var2.getLocation().add((double)0.0F, (double)var22, (double)0.0F), Effect.ENDER_SIGNAL, 1);
                     }

                     final int var23 = Utils.random.nextInt(22) + 18;
                     (new BukkitRunnable() {
                        int slot = 0;

                        public void run() {
                           if (this.slot > 0) {
                              var16.setItem(this.slot - 1, (ItemStack)null);
                           }

                           var16.setItem(this.slot, (ItemStack)var5.get(Utils.random.nextInt(var5.size())));
                           if (this.slot != var23) {
                              ++this.slot;
                              var2.playSound(var2.getLocation(), SoundsManager.CLICK, 1.0F, 1.0F);
                           } else {
                              if (var2.isOnline()) {
                                 var2.sendMessage(((String)KBListener.this.plugin.msgs.messages.get("Player-Win-Kit")).replace("%kit%", ChatColor.stripColor(var16.getItem(this.slot).getItemMeta().getDisplayName())));
                                 Kit var1 = (Kit)KBListener.this.plugin.Kits.get(ChatColor.stripColor(var16.getItem(this.slot).getItemMeta().getDisplayName().toLowerCase()));
                                 if (var9.kitsInventory.getAllContents().contains(var1.getLogo())) {
                                    int var2x = (int)((double)var1.getPrice() * KBListener.this.plugin.config.SellValue);
                                    var9.addCoins(var2, var2x);
                                    if (var9.customScoreboard != null) {
                                       var9.customScoreboard.updatePlaceholder("%coins%", var9.getCoins(var2));
                                    }

                                    var2.sendMessage((String)KBListener.this.plugin.msgs.messages.get("Kit-Already-Unlocked") + ChatColor.GREEN + " (+" + var2x + ")");
                                 } else {
                                    Map.Entry var4 = var9.kitsInventory.getEmptySlot();
                                    var9.kitsInventory.setItem((Integer)var4.getKey(), (Integer)var4.getValue(), var1.getLogo());
                                 }

                                 for(Location var3 : Utils.getSurroundingLocations(var2.getLocation())) {
                                    KBListener.this.spawnFirework(var3);
                                 }
                              } else {
                                 (new BukkitRunnable() {
                                    public void run() {
                                       String var1 = KBListener.this.plugin.config.UUID ? var2.getUniqueId().toString() : var2.getName();
                                       if (KBListener.this.plugin.config.useMySQL) {
                                          try {
                                             Statement var2x = KBListener.this.plugin.mysql.getConnection().createStatement();
                                             ResultSet var3 = var2x.executeQuery("SELECT Kits FROM " + KBListener.this.plugin.config.tableprefix + " WHERE " + (KBListener.this.plugin.config.UUID ? "player_uuid" : "player_name") + " = '" + var1 + "';");
                                             var3.next();
                                             String var4 = var3.getString("Kits");
                                             var4 = var4 + ", " + ChatColor.stripColor(var16.getItem(slot).getItemMeta().getDisplayName());
                                             KBListener.this.plugin.mysql.getConnection().prepareStatement("UPDATE " + KBListener.this.plugin.config.tableprefix + " SET Kits='" + var4 + "' WHERE " + (KBListener.this.plugin.config.UUID ? "player_uuid" : "player_name") + "='" + var1 + "';").executeUpdate();
                                          } catch (SQLException var7) {
                                             var7.printStackTrace();
                                          }
                                       } else {
                                          File var8 = new File(KBListener.this.plugin.getDataFolder() + "/players/", var1);
                                          if (var8.exists()) {
                                             YamlConfiguration var9x = YamlConfiguration.loadConfiguration(var8);
                                             List var11 = ((FileConfiguration)var9x).getStringList("Kits");
                                             var11.add(ChatColor.stripColor(var16.getItem(slot).getItemMeta().getDisplayName()));
                                             ((FileConfiguration)var9x).set("Kits", var11);

                                             try {
                                                ((FileConfiguration)var9x).save(var8);
                                             } catch (IOException var6) {
                                                var6.printStackTrace();
                                             }
                                          }
                                       }

                                    }
                                 }).runTaskAsynchronously(KBListener.this.plugin);
                              }

                              this.cancel();
                           }
                        }
                     }).runTaskTimer(this.plugin, 0L, 3L);
                  }
               }
            }
         }

      }
   }

   @EventHandler
   public void blockCommand(PlayerCommandPreprocessEvent var1) {
      Player var2 = var1.getPlayer();
      if (!var2.hasPermission("VexlioKits.unblockcmd")) {
         if (this.plugin.players.contains(var2.getUniqueId()) && !this.plugin.config.allowedCommands.contains(var1.getMessage().split(" ")[0].replace("/", "").toLowerCase())) {
            var1.setCancelled(true);
            var2.sendMessage((String)this.plugin.msgs.messages.get("Cant-Use-Commands"));
         }

      }
   }

   @EventHandler
   public void onPlayerMoveEvent(PlayerMoveEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.plugin.players.contains(var2.getUniqueId())) {
         if (var1.getTo().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.SPONGE)) {
            var2.setVelocity(new Vector((double)0.0F, this.plugin.config.SpongeBoostUpwards, (double)0.0F));
            var2.setFallDistance((float)(-this.plugin.config.SpongeFallProtection));
            if (this.plugin.config.SpongeLaunchSound) {
               var2.playSound(var2.getLocation(), SoundsManager.WITHER_SHOOT, 10.0F, 10.0F);
            }
         }

      }
   }

   @EventHandler
   public void onEntityDamage(EntityDamageEvent var1) {
      if (var1.getEntity().getType() == EntityType.PLAYER) {
         Player var2 = (Player)var1.getEntity();
         if (this.plugin.players.contains(var2.getUniqueId())) {
            if (var1.getCause().equals(DamageCause.VOID) && this.plugin.config.VoidInstantDeath) {
               var1.setDamage((double)1000.0F);
            } else if (var1.getCause().equals(DamageCause.FALL) && !this.plugin.config.FallDamageEnabled) {
               var1.setCancelled(true);
            } else {
               PlayerData var3 = PlayerDataManager.get(var2);
               if (var3.teleportTask != null) {
                  var2.sendMessage((String)this.plugin.msgs.messages.get("Player-Damage"));
                  var3.teleportTask.cancel();
                  var3.teleportTask = null;
               }
            }
         }

      }
   }

   @EventHandler
   public void onEntityDamageByEntity(EntityDamageByEntityEvent var1) {
      if (!var1.isCancelled()) {
         if (var1.getEntityType().equals(EntityType.PLAYER) && this.plugin.players.contains(var1.getEntity().getUniqueId()) || var1.getEntity().hasMetadata("toRemove")) {
            if (var1.getDamager().getType().equals(EntityType.FIREWORK)) {
               var1.setCancelled(true);
            } else {
               Player var2 = null;
               if (var1.getDamager().getType().equals(EntityType.PLAYER)) {
                  var2 = (Player)var1.getDamager();
               } else if (var1.getDamager() instanceof Projectile && ((Projectile)var1.getDamager()).getShooter() instanceof Player) {
                  var2 = (Player)((Projectile)var1.getDamager()).getShooter();
               } else if (var1.getDamager().hasMetadata("toRemove")) {
                  LivingEntity var3 = (LivingEntity)var1.getDamager();
                  String var4 = ChatColor.stripColor(var3.getCustomName().split("'s")[0]);
                  var2 = Bukkit.getPlayer(var4);
               }

               if (var2 != null) {
                  if (var1.getEntityType().equals(EntityType.PLAYER)) {
                     if (var1.getDamage() < (double)1.0F) {
                        return;
                     }

                     Player var5 = (Player)var1.getEntity();
                     if (var2.getName().equals(var5.getName())) {
                        return;
                     }

                     if (this.plugin.players.contains(var5.getUniqueId()) && this.plugin.players.contains(var2.getUniqueId())) {
                        if (var1.getDamager() instanceof Projectile) {
                           PlayerDataManager.get(var2).addProjectileHits(var2);
                        }

                        PlayerData var7 = PlayerDataManager.get(var5);
                        var7.addDamage(var2, var1.getDamage());
                        if (this.plugin.config.combatLogEnabled) {
                           if (this.plugin.config.combatLogMessage && System.currentTimeMillis() - var7.lastHitTime >= (long)this.plugin.config.combatLogDuration * 1000L) {
                              var5.sendMessage(((String)this.plugin.msgs.messages.get("Combat-Engage")).replace("%seconds%", String.valueOf(this.plugin.config.combatLogDuration)));
                           }

                           var7.lastHitTime = System.currentTimeMillis();
                           var7.lastHit = var2.getName();
                        }
                     }
                  } else {
                     LivingEntity var6 = (LivingEntity)var1.getEntity();
                     if (var6.getCustomName() != null) {
                        String var8 = var6.getCustomName().split("'s ")[0];
                        if (var2.getName().equals(var8)) {
                           var1.setCancelled(true);
                        }
                     }
                  }

               }
            }
         }
      }
   }

   @EventHandler
   public void onEntityShootBowEvent(ProjectileLaunchEvent var1) {
      final Projectile var2 = var1.getEntity();
      if (var2.getType().equals(EntityType.ARROW) || var2.getType().equals(EntityType.SNOWBALL)) {
         if (var2.getShooter() != null && var2.getShooter() instanceof Player) {
            Player var3 = (Player)var2.getShooter();
            if (this.plugin.players.contains(var3.getUniqueId())) {
               PlayerData var4 = PlayerDataManager.get(var3);
               if (var4.selectedTrail != null) {
                  final World var5 = var3.getWorld();
                  final Effect var6 = var4.selectedTrail;
                  (new BukkitRunnable() {
                     int runs = 0;

                     public void run() {
                        var5.playEffect(var2.getLocation(), var6, 1);

                        for(int var1 = 1; var1 < KBListener.this.plugin.config.TrailsSize; ++var1) {
                           var5.playEffect(var2.getLocation().add(Utils.random.nextDouble(), Utils.random.nextDouble(), Utils.random.nextDouble()), var6, 1);
                        }

                        ++this.runs;
                        if (!var2.isValid() || var2.isOnGround() || var2.getLocation().getY() < (double)0.0F || this.runs >= 30) {
                           this.cancel();
                        }

                     }
                  }).runTaskTimer(this.plugin, 0L, (long)this.plugin.config.TrailsInterval);
               }
            }
         }

      }
   }

   @EventHandler
   public void onPlayerEntityInteractEvent(PlayerInteractEntityEvent var1) {
      Player var2 = var1.getPlayer();
      if (var1.getRightClicked().getType().equals(EntityType.PLAYER)) {
         Player var3 = (Player)var1.getRightClicked();
         if (this.plugin.players.contains(var2.getUniqueId()) && this.plugin.players.contains(var3.getUniqueId()) && Utils.compareItem(var2.getItemInHand(), ((Config.HotbarItem)VexlioKits.getInstance().config.hotBarItems.get("Challenges-Item")).item)) {
            var2.closeInventory();
            if (this.plugin.challengesManager != null) {
               if (!this.plugin.challengesManager.hasQueue(1)) {
                  return;
               }

               if (this.privateDuels.containsKey(var3.getName()) && ((String)this.privateDuels.get(var3.getName())).equals(var2.getName())) {
                  var3.sendMessage((String)this.plugin.msgs.messages.get("Duel-Accept"));
                  var2.sendMessage((String)this.plugin.msgs.messages.get("Duel-Accept"));
                  this.plugin.challengesManager.remove(var2, 1);
                  this.plugin.challengesManager.remove(var3, 1);
                  this.plugin.challengesManager.addDuelers(var2, var3);
                  this.privateDuels.remove(var3.getName());
               } else {
                  PlayerData var4 = PlayerDataManager.get(var2);
                  if (var4.hasCooldown(var2, "PRIVATE_DUEL")) {
                     return;
                  }

                  var4.setCooldown(var2, "PRIVATE_DUEL", 10, false);
                  this.privateDuels.put(var2.getName(), var3.getName());
                  var2.sendMessage(((String)this.plugin.msgs.messages.get("Duel-Send")).replace("%receiver%", var3.getName()));
                  var3.sendMessage(((String)this.plugin.msgs.messages.get("Duel-Receive")).replace("%sender%", var2.getName()));
               }

               var2.playSound(var2.getLocation(), SoundsManager.CLICK, 1.0F, 1.0F);
            } else {
               var2.setItemInHand(new ItemStack(Material.AIR));
            }

         }
      }
   }

   @EventHandler
   public void onSignChangeEvent(SignChangeEvent var1) {
      if (var1.getLine(0).equalsIgnoreCase("[kb]")) {
         Player var2 = var1.getPlayer();
         if (!var2.hasPermission("VexlioKits.createsigns")) {
            var2.sendMessage((String)this.plugin.msgs.messages.get("No-Permission"));
         } else {
            String var3 = var1.getLine(1).toLowerCase();
            if (!var3.equals("join") && !var3.equals("leave") && !var3.equals("soup") && !var3.equals("potions") && !var3.equals("stats") && !var3.equals("top")) {
               var2.sendMessage(this.plugin.msgs.prefix + "Line 2 must be one of those options join/leave/soup/potions/top!");
            } else if (var3.equals("join")) {
               String var4 = var1.getLine(2).toLowerCase();
               if (this.plugin.playingMaps.containsKey(var4)) {
                  final PlayingMap var5 = (PlayingMap)this.plugin.playingMaps.get(var4);
                  FileConfiguration var6 = this.plugin.fileManager.getConfig("maps.yml");

                  int var7;
                  for(var7 = var6.getConfigurationSection("Maps." + var5.name + ".Signs") != null && !var6.getConfigurationSection("Maps." + var5.name + ".Signs").getKeys(false).isEmpty() ? var6.getConfigurationSection("Maps." + var5.name + ".Signs").getKeys(false).size() + 1 : 1; var6.contains("Maps." + var5.name + ".Signs." + var7); ++var7) {
                  }

                  var6.set("Maps." + var5.name + ".Signs." + var7, Utils.getStringFromLocation(var1.getBlock().getLocation(), false));
                  this.plugin.fileManager.saveConfig("maps.yml");
                  var5.signs.put(var1.getBlock().getLocation(), var7);
                  var1.setLine(0, this.plugin.config.SignsPrefix);
                  var1.setLine(1, this.plugin.config.JoinPrefix);
                  var1.setLine(2, this.plugin.config.JoinLine3Color + var5.name);
                  (new BukkitRunnable() {
                     public void run() {
                        var5.updateSignPlayers();
                     }
                  }).runTaskLater(this.plugin, 5L);
                  var2.sendMessage(this.plugin.msgs.prefix + "You have created a new join sign for the map " + var5.name + " with the id of #" + ChatColor.AQUA + var7 + "!");
               } else {
                  var2.sendMessage(this.plugin.msgs.prefix + "Line 3 should be a joinable map!");
               }
            } else if (var3.equals("leave")) {
               var1.setLine(0, this.plugin.config.SignsPrefix);
               var1.setLine(1, this.plugin.config.LeavePrefix);
               var2.sendMessage(this.plugin.msgs.prefix + "You have created a leave sign!");
            } else if (var3.equals("soup")) {
               var1.setLine(0, this.plugin.config.SignsPrefix);
               var1.setLine(1, this.plugin.config.SoupPrefix);
               var2.sendMessage(this.plugin.msgs.prefix + "You have created a soup sign!");
            } else if (var3.equals("stats")) {
               var1.setLine(0, this.plugin.config.SignsPrefix);
               var1.setLine(1, this.plugin.config.StatsPrefix);
               var2.sendMessage(this.plugin.msgs.prefix + "You have created a stats sign!");
            } else if (var3.equals("potions")) {
               var1.setLine(0, this.plugin.config.SignsPrefix);
               var1.setLine(1, this.plugin.config.PotionsPrefix);
               var2.sendMessage(this.plugin.msgs.prefix + "You have created a potions sign!");
            } else if (var3.equals("top")) {
               String var10 = var1.getLine(2).toUpperCase();
               boolean var11 = false;

               for(VexlioKitsAPI.Stat var9 : VexlioKitsAPI.Stat.values()) {
                  if (var9.name().equals(var10)) {
                     var11 = true;
                     break;
                  }
               }

               if (!var11) {
                  var1.setLine(2, ChatColor.DARK_RED + "Invalid");
                  String var15 = ChatColor.GREEN + VexlioKitsAPI.Stat.values()[0].name();

                  for(int var18 = 1; var18 < VexlioKitsAPI.Stat.values().length; ++var18) {
                     var15 = var15 + ChatColor.YELLOW + ", " + ChatColor.GREEN + VexlioKitsAPI.Stat.values()[var18].name();
                  }

                  var2.sendMessage(this.plugin.msgs.prefix + "Invalid stat! use one of the following stats: " + ChatColor.YELLOW + var15);
                  return;
               }

               int var13 = 0;
               if (Utils.checkNumbers(var1.getLine(3)) && (var13 = Integer.valueOf(var1.getLine(3))) >= 1) {
                  FileConfiguration var17 = this.plugin.fileManager.getConfig("signs.yml");
                  Location var19 = var1.getBlock().getLocation();
                  int var20 = 1;
                  if (var17.getConfigurationSection("Signs.Top") != null && !var17.getConfigurationSection("Signs.Top").getKeys(false).isEmpty()) {
                     var20 = var17.getConfigurationSection("Signs.Top").getKeys(false).size() + 1;
                     if (var17.contains("Signs.Top." + var20)) {
                        var20 = 1;
                     }

                     while(var17.contains("Signs.Top." + var20)) {
                        ++var20;
                     }
                  }

                  var17.set("Signs.Top." + var20 + ".world", var19.getWorld().getName());
                  var17.set("Signs.Top." + var20 + ".x", var19.getBlockX());
                  var17.set("Signs.Top." + var20 + ".y", var19.getBlockY());
                  var17.set("Signs.Top." + var20 + ".z", var19.getBlockZ());
                  this.plugin.fileManager.saveConfig("signs.yml");
                  this.plugin.topSigns.put(var19, var20);
                  var1.setLine(0, ChatColor.AQUA + "Top #" + ChatColor.RED + var13);
                  var1.setLine(1, var1.getLine(2));
                  var1.setLine(2, "Waiting...");
                  var1.setLine(3, "(0)");
                  this.plugin.startLeaderboardUpdater();
                  var2.sendMessage(this.plugin.msgs.prefix + "You have " + ChatColor.GREEN + "successfully" + ChatColor.GOLD + " created a " + ChatColor.AQUA + "top" + ChatColor.GOLD + " sign with the id of " + ChatColor.LIGHT_PURPLE + "#" + var20 + ChatColor.GOLD + "! use /sw list to check when their next update is!");
                  return;
               }

               var1.setLine(3, ChatColor.DARK_RED + "Invalid");
               var2.sendMessage(this.plugin.msgs.prefix + "Fourth line is the rank you are looking for!");
               return;
            }

         }
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void onPlayerChatEvent(AsyncPlayerChatEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.plugin.players.contains(var2.getUniqueId()) && this.plugin.config.ShowRankInChat) {
         Rank var3 = PlayerDataManager.get(var2).getRank();
         var1.setFormat(var3.getPrefix() + ChatColor.RESET + var1.getFormat());
      }
   }

   @EventHandler
   public void onCommand(PlayerCommandPreprocessEvent var1) {
      String var2 = var1.getMessage().replace("/", "").split(" ")[0].toLowerCase();

      for(String var4 : this.plugin.config.aliases) {
         if (var4.equals(var2)) {
            var1.setMessage(var1.getMessage().replace(var1.getMessage().split(" ")[0], "/VexlioKits"));
            return;
         }
      }

   }

   @EventHandler
   public void AbilityEntityExplode(EntityExplodeEvent var1) {
      if (var1.getEntity() != null && var1.getEntity().hasMetadata("tnts")) {
         var1.blockList().clear();
      }

   }

   @EventHandler
   public void AbilityEntityDeathEvent(EntityDeathEvent var1) {
      if (var1.getEntity().hasMetadata("toRemove")) {
         var1.setDroppedExp(0);
         var1.getDrops().clear();
      }

   }

   @EventHandler
   public void onEntityTargetEvent(EntityTargetEvent var1) {
      if (var1.getTarget() != null) {
         if (var1.getEntity() instanceof LivingEntity && var1.getTarget().getType().equals(EntityType.PLAYER)) {
            LivingEntity var2 = (LivingEntity)var1.getEntity();
            if (var2.hasMetadata("toRemove") && var2.getCustomName() != null) {
               String var3 = var2.getCustomName().split("'s ")[0];
               if (var1.getTarget().getName().equals(var3)) {
                  var1.setCancelled(true);
               }
            }
         }

      }
   }

   public void spawnFirework(Location var1) {
      Firework var2 = (Firework)var1.getWorld().spawn(var1, Firework.class);
      FireworkMeta var3 = var2.getFireworkMeta();
      FireworkEffect.Type var4 = Type.BALL;
      FireworkEffect var5 = FireworkEffect.builder().flicker(Utils.random.nextBoolean()).withColor(Color.fromBGR(Utils.random.nextInt(256), Utils.random.nextInt(256), Utils.random.nextInt(256))).withFade(Color.fromBGR(Utils.random.nextInt(256), Utils.random.nextInt(256), Utils.random.nextInt(256))).with(var4).trail(Utils.random.nextBoolean()).build();
      var3.addEffect(var5);
      var3.setPower(0);
      var2.setFireworkMeta(var3);
   }

   public String getDeathMessage(Player var1, Player var2) {
      if (var2 != null) {
         if (var2.getName().equals(var1.getName())) {
            return ((String)this.plugin.msgs.deathMessages.get("SUICIDE")).replace("%player%", var1.getName());
         } else {
            String var3 = PlayerDataManager.get(var2).getKit() != null ? PlayerDataManager.get(var2).getKit().name : "No Kit";
            return ((String)this.plugin.msgs.killMessages.get(Utils.random.nextInt(this.plugin.msgs.killMessages.size()))).replace("%player%", var1.getName()).replace("%killer%", var2.getName()).replace("%killerkit%", var3);
         }
      } else {
         return var1.getLastDamageCause() != null && this.plugin.msgs.deathMessages.containsKey(var1.getLastDamageCause().getCause().name()) ? ((String)this.plugin.msgs.deathMessages.get(var1.getLastDamageCause().getCause().name())).replace("%player%", var1.getName()) : ((String)this.plugin.msgs.deathMessages.get("UNKNOWN")).replace("%player%", var1.getName());
      }
   }
}
