package me.wazup.kitbattle.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.wazup.kitbattle.Config;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.Messages;
import me.wazup.kitbattle.commands.admin.AddSpawnCommand;
import me.wazup.kitbattle.commands.admin.AddSpawnCuboid;
import me.wazup.kitbattle.commands.admin.AdminCommand;
import me.wazup.kitbattle.commands.admin.CoinsCommand;
import me.wazup.kitbattle.commands.admin.CreateCommand;
import me.wazup.kitbattle.commands.admin.DeleteCommand;
import me.wazup.kitbattle.commands.admin.DisableCommand;
import me.wazup.kitbattle.commands.admin.EditModeCommand;
import me.wazup.kitbattle.commands.admin.EnableCommand;
import me.wazup.kitbattle.commands.admin.ExpCommand;
import me.wazup.kitbattle.commands.admin.HologramsCommand;
import me.wazup.kitbattle.commands.admin.KitCommand;
import me.wazup.kitbattle.commands.admin.KitUnlockerCommand;
import me.wazup.kitbattle.commands.admin.ReloadCommand;
import me.wazup.kitbattle.commands.admin.RemoveSpawnCommand;
import me.wazup.kitbattle.commands.admin.RemoveSpawnCuboid;
import me.wazup.kitbattle.commands.admin.ResetCommand;
import me.wazup.kitbattle.commands.admin.TestCommand;
import me.wazup.kitbattle.commands.admin.WandCommand;
import me.wazup.kitbattle.commands.user.BountyCommand;
import me.wazup.kitbattle.commands.user.GuiCommand;
import me.wazup.kitbattle.commands.user.InfoCommand;
import me.wazup.kitbattle.commands.user.JoinCommand;
import me.wazup.kitbattle.commands.user.LeaveCommand;
import me.wazup.kitbattle.commands.user.ListCommand;
import me.wazup.kitbattle.commands.user.SelectKitCommand;
import me.wazup.kitbattle.commands.user.SpawnCommand;
import me.wazup.kitbattle.commands.user.SpectateCommand;
import me.wazup.kitbattle.commands.user.StatsCommand;
import me.wazup.kitbattle.commands.user.ToggleTournamentCommand;
import me.wazup.kitbattle.commands.user.VoteCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor, TabCompleter {
   HashMap<String, SubCommand> commands = new HashMap();

   public MainCommand() {
      this.commands.put("join", new JoinCommand());
      this.commands.put("leave", new LeaveCommand());
      this.commands.put("stats", new StatsCommand());
      this.commands.put("vote", new VoteCommand());
      this.commands.put("selectkit", new SelectKitCommand());
      this.commands.put("gui", new GuiCommand());
      this.commands.put("list", new ListCommand());
      this.commands.put("spectate", new SpectateCommand());
      this.commands.put("spawn", new SpawnCommand());
      this.commands.put("bounty", new BountyCommand());
      this.commands.put("info", new InfoCommand());
      this.commands.put("toggletournament", new ToggleTournamentCommand());
      this.commands.put("admin", new AdminCommand());
      this.commands.put("holograms", new HologramsCommand());
      this.commands.put("reload", new ReloadCommand());
      this.commands.put("create", new CreateCommand());
      this.commands.put("delete", new DeleteCommand());
      this.commands.put("addspawn", new AddSpawnCommand());
      this.commands.put("removespawn", new RemoveSpawnCommand());
      this.commands.put("reset", new ResetCommand());
      this.commands.put("wand", new WandCommand());
      this.commands.put("addspawncuboid", new AddSpawnCuboid());
      this.commands.put("removespawncuboid", new RemoveSpawnCuboid());
      this.commands.put("kit", new KitCommand());
      this.commands.put("kitunlocker", new KitUnlockerCommand());
      this.commands.put("editmode", new EditModeCommand());
      this.commands.put("enable", new EnableCommand());
      this.commands.put("disable", new DisableCommand());
      this.commands.put("coins", new CoinsCommand());
      this.commands.put("exp", new ExpCommand());
      this.commands.put("test", new TestCommand());
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      Kitbattle var5 = Kitbattle.getInstance();
      if (var4.length == 0) {
         var1.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + " ----------" + ChatColor.AQUA + " KitBattle " + ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + "----------");
         var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Main command");
         var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "List" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Shows a list of maps");
         if (!Config.getInstance().bungeeMode) {
            var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Join" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Join the selected map");
            var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Leave" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Leave your current map");
         } else {
            var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Vote" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Vote for the next map");
         }

         var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Stats" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Shows a player stats");
         var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Gui" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Opens up a gui");
         var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Selectkit" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Selects the targeted kit");
         var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Bounty" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Set a bounty on a target!");
         var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Spectate" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Puts you in the spectator mode");
         var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Spawn" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Teleport back to spawn!");
         var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "ToggleTournament" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Enters/Quits the tournament queue");
         var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.GREEN + "Info" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Shows some information");
         var1.sendMessage(ChatColor.DARK_AQUA + " - " + ChatColor.AQUA + "/kb " + ChatColor.RED + "Admin" + ChatColor.DARK_AQUA + " - " + ChatColor.GRAY + "Shows the admin commands");
         var1.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + " ----------------------------");
         return true;
      } else {
         String var6 = var4[0].toLowerCase();
         Messages var7 = Messages.getInstance();
         if (!this.commands.containsKey(var6)) {
            boolean var8 = false;

            for(String var10 : this.commands.keySet()) {
               if (var10.startsWith(var6)) {
                  var6 = var10;
                  var8 = true;
                  break;
               }
            }

            if (!var8) {
               var1.sendMessage((String)var7.messages.get("Unknown-Command"));
               return true;
            }
         }

         SubCommand var14 = (SubCommand)this.commands.get(var6);
         if (!var14.allowConsole && !(var1 instanceof Player)) {
            var1.sendMessage((String)var7.messages.get("No-Console"));
            return true;
         } else if (var14.permission != null && !var1.hasPermission(var14.permission)) {
            var1.sendMessage((String)var7.messages.get("No-Permission"));
            return false;
         } else {
            boolean var15 = var14.execute(var5, var7, var1, var4);
            if (!var15) {
               var1.sendMessage(var7.prefix + "Usage: /kb " + ChatColor.GREEN + var4[0] + ChatColor.YELLOW + " " + var14.arguments);

               for(String var13 : var14.argumentsExplaination) {
                  var1.sendMessage(var7.prefix + ChatColor.AQUA + "- " + ChatColor.GRAY + var13);
               }
            }

            return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length != 1) {
         String var9 = var4[0].toLowerCase();
         if (var9.equals("join")) {
            ArrayList var10 = new ArrayList(Kitbattle.getInstance().playingMaps.keySet());
            return var10;
         } else {
            return null;
         }
      } else {
         ArrayList var5 = new ArrayList();

         for(String var7 : this.commands.keySet()) {
            SubCommand var8 = (SubCommand)this.commands.get(var7);
            if (var8.permission == null || var1.hasPermission(var8.permission)) {
               var5.add(var7);
            }
         }

         return var5;
      }
   }
}
