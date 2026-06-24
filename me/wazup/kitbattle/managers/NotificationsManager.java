package me.wazup.kitbattle.managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class NotificationsManager implements Listener {
   private final String pluginName = "KitBattle";
   private final String permission = "kitbattle.admin";
   private final String url = "https://musing-jennings-3f352c.netlify.app/";
   private final long fetchInterval = 21600000L;
   private final long notificationInterval = 21600000L;
   private boolean notificationAvailable;
   private final List<String> messages = new ArrayList();
   private long lastCheck = 0L;
   private final HashMap<String, Long> notifiedPlayers = new HashMap();

   public NotificationsManager() {
      Bukkit.getPluginManager().registerEvents(this, Kitbattle.getInstance());
   }

   public void fetch() {
      this.lastCheck = System.currentTimeMillis();

      try {
         BufferedReader var1 = new BufferedReader(new InputStreamReader((new URL("https://musing-jennings-3f352c.netlify.app/")).openStream()));
         this.notificationAvailable = false;
         String var2 = var1.readLine();
         String var3 = var2.replace("enabled-plugins: ", "").toLowerCase();
         if (var3.contains("all_plugins") || var3.contains("KitBattle".toLowerCase())) {
            this.notificationAvailable = true;
         }

         if (!this.notificationAvailable) {
            return;
         }

         this.messages.clear();

         while((var2 = var1.readLine()) != null) {
            this.messages.add(Utils.colorize(var2).replace("%permission%", "kitbattle.admin").replace("%plugin%", "KitBattle"));
         }
      } catch (IOException var4) {
         this.notificationAvailable = false;
      }

   }

   public void notifyPlayer(Player var1) {
      if (this.notificationAvailable && System.currentTimeMillis() - (Long)this.notifiedPlayers.getOrDefault(var1.getName(), 0L) >= 21600000L) {
         this.notifiedPlayers.put(var1.getName(), System.currentTimeMillis());

         for(String var3 : this.messages) {
            var1.sendMessage(var3);
         }
      }

   }

   @EventHandler
   public void onPlayerJoinEvent(PlayerJoinEvent var1) {
      final Player var2 = var1.getPlayer();
      if (var2.hasPermission("kitbattle.admin")) {
         if (this.lastCheck != 0L && System.currentTimeMillis() - this.lastCheck < 21600000L) {
            this.notifyPlayer(var2);
         } else {
            (new BukkitRunnable() {
               public void run() {
                  NotificationsManager.this.fetch();
                  NotificationsManager.this.notifyPlayer(var2);
               }
            }).runTaskAsynchronously(Kitbattle.getInstance());
         }
      }

   }
}
