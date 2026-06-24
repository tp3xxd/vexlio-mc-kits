package me.wazup.kitbattle.abilities;

import java.util.HashMap;
import me.wazup.kitbattle.Kit;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.abilities.list.BaneAbility;
import me.wazup.kitbattle.abilities.list.BlinkerAbility;
import me.wazup.kitbattle.abilities.list.BuilderAbility;
import me.wazup.kitbattle.abilities.list.BurrowerAbility;
import me.wazup.kitbattle.abilities.list.CentaurAbility;
import me.wazup.kitbattle.abilities.list.ClimberAbility;
import me.wazup.kitbattle.abilities.list.DraculaAbility;
import me.wazup.kitbattle.abilities.list.DragonAbility;
import me.wazup.kitbattle.abilities.list.FishermanAbility;
import me.wazup.kitbattle.abilities.list.HadesAbility;
import me.wazup.kitbattle.abilities.list.HulkAbility;
import me.wazup.kitbattle.abilities.list.KangarooAbility;
import me.wazup.kitbattle.abilities.list.MonkAbility;
import me.wazup.kitbattle.abilities.list.PhantomAbility;
import me.wazup.kitbattle.abilities.list.PrisonerAbility;
import me.wazup.kitbattle.abilities.list.RiderAbility;
import me.wazup.kitbattle.abilities.list.SouperAbility;
import me.wazup.kitbattle.abilities.list.SpidermanAbility;
import me.wazup.kitbattle.abilities.list.StomperAbility;
import me.wazup.kitbattle.abilities.list.SuicidalAbility;
import me.wazup.kitbattle.abilities.list.SummonerAbility;
import me.wazup.kitbattle.abilities.list.SunderAbility;
import me.wazup.kitbattle.abilities.list.SwitcherAbility;
import me.wazup.kitbattle.abilities.list.ThorAbility;
import me.wazup.kitbattle.abilities.list.TimelordAbility;
import me.wazup.kitbattle.abilities.list.ViperAbility;
import me.wazup.kitbattle.abilities.list.ZenAbility;
import me.wazup.kitbattle.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AbilityManager {
   private static AbilityManager instance;
   public HashMap<String, Ability> abilities;

   public AbilityManager() {
      instance = this;
      this.abilities = new HashMap();
      this.registerAbility(new KangarooAbility());
      this.registerAbility(new ViperAbility());
      this.registerAbility(new BlinkerAbility());
      this.registerAbility(new FishermanAbility());
      this.registerAbility(new PrisonerAbility());
      this.registerAbility(new DraculaAbility());
      this.registerAbility(new SpidermanAbility());
      this.registerAbility(new SouperAbility());
      this.registerAbility(new HulkAbility());
      this.registerAbility(new RiderAbility());
      this.registerAbility(new SummonerAbility());
      this.registerAbility(new ZenAbility());
      this.registerAbility(new BurrowerAbility());
      this.registerAbility(new TimelordAbility());
      this.registerAbility(new PhantomAbility());
      this.registerAbility(new DragonAbility());
      this.registerAbility(new SuicidalAbility());
      this.registerAbility(new HadesAbility());
      this.registerAbility(new ThorAbility());
      this.registerAbility(new CentaurAbility());
      this.registerAbility(new StomperAbility());
      this.registerAbility(new SwitcherAbility());
      this.registerAbility(new ClimberAbility());
      this.registerAbility(new SunderAbility());
      this.registerAbility(new BaneAbility());
      this.registerAbility(new MonkAbility());
      this.registerAbility(new BuilderAbility());
      Bukkit.getPluginManager().registerEvents(new AbilityListener(Kitbattle.getInstance()), Kitbattle.getInstance());
      Bukkit.getPluginManager().registerEvents(new SpecialAbilityListener(Kitbattle.getInstance()), Kitbattle.getInstance());
   }

   public static AbilityManager getInstance() {
      return instance;
   }

   public void registerAbility(Ability var1) {
      this.abilities.put(var1.getName().toLowerCase(), var1);
   }

   public void loadAbilityConfig(Ability var1) {
      var1.load(Kitbattle.getInstance().fileManager.getConfig("abilities.yml"));
   }

   public void loadAbilitiesConfig() {
      FileConfiguration var1 = Kitbattle.getInstance().fileManager.getConfig("abilities.yml");

      for(Ability var3 : this.abilities.values()) {
         var3.load(var1);
      }

   }

   public boolean hasInteractionAbility(Player var1, String var2) {
      Kit var3 = PlayerDataManager.get(var1).getKit();
      if (var3 != null) {
         for(Ability var5 : var3.getInteractionAbilities()) {
            if (var5.getName().equals(var2)) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean hasSpecialAbility(Player var1, String var2) {
      Kit var3 = PlayerDataManager.get(var1).getKit();
      if (var3 != null) {
         for(Ability var5 : var3.getOtherAbilities()) {
            if (var5.getName().equals(var2)) {
               return true;
            }
         }
      }

      return false;
   }

   public Ability getAbility(String var1) {
      return (Ability)this.abilities.get(var1.toLowerCase());
   }

   public void updateKitAbilities() {
      for(Kit var2 : Kitbattle.getInstance().Kits.values()) {
         var2.loadAbilities();
      }

   }
}
