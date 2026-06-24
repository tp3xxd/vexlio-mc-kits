package me.wazup.kitbattle.managers;

import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import me.wazup.kitbattle.Kitbattle;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TitleManager {
   private static TitleManager instance;
   boolean titles;
   boolean actionBar;
   TitleManagerAPI api;

   public TitleManager(Kitbattle var1, boolean var2, boolean var3) {
      instance = this;
      if (var2 || var3) {
         if (Bukkit.getPluginManager().isPluginEnabled("TitleManager")) {
            this.api = (TitleManagerAPI)Bukkit.getServer().getPluginManager().getPlugin("TitleManager");
            this.actionBar = var3;
            this.titles = var2;
            Bukkit.getConsoleSender().sendMessage(var1.msgs.prefix + "Hooked into TitleManager!");
         } else {
            try {
               Player.class.getMethod("sendTitle", String.class, String.class);
               this.titles = var2;
            } catch (NoSuchMethodException var6) {
               Bukkit.getConsoleSender().sendMessage(var1.msgs.prefix + "Titles are not supported on this server!");
            }

            try {
               Class.forName("org.bukkit.entity.Player$Spigot").getMethod("sendMessage", ChatMessageType.class, BaseComponent.class);
               this.actionBar = var3;
            } catch (ClassNotFoundException | NoSuchMethodException var5) {
               Bukkit.getConsoleSender().sendMessage(var1.msgs.prefix + "Action bar is not supported on this server!");
            }
         }

      }
   }

   public static TitleManager getInstance() {
      return instance;
   }

   public boolean sendTitle(Player var1, String var2) {
      return this.sendTitle(var1, var2, "");
   }

   public boolean sendTitle(Player var1, String var2, String var3) {
      if (!this.titles) {
         return false;
      } else {
         if (this.api != null) {
            this.api.sendTitles(var1, var2, var3);
         } else {
            var1.sendTitle(var2, var3);
         }

         return true;
      }
   }

   public void sendActionBar(Player var1, String var2) {
      if (this.actionBar) {
         if (this.api != null) {
            this.api.sendActionbar(var1, var2);
         } else {
            var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var2));
         }

      }
   }

   public boolean isActionBarEnabled() {
      return this.actionBar;
   }
}
