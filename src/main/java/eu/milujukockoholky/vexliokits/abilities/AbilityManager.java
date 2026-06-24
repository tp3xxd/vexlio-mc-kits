package eu.milujukockoholky.vexliokits.abilities;

import java.util.HashMap;
import eu.milujukockoholky.vexliokits.Kit;
import eu.milujukockoholky.vexliokits.VexlioKits;
import eu.milujukockoholky.vexliokits.abilities.list.BaneAbility;
import eu.milujukockoholky.vexliokits.abilities.list.BlinkerAbility;
import eu.milujukockoholky.vexliokits.abilities.list.BuilderAbility;
import eu.milujukockoholky.vexliokits.abilities.list.BurrowerAbility;
import eu.milujukockoholky.vexliokits.abilities.list.CentaurAbility;
import eu.milujukockoholky.vexliokits.abilities.list.ClimberAbility;
import eu.milujukockoholky.vexliokits.abilities.list.DraculaAbility;
import eu.milujukockoholky.vexliokits.abilities.list.DragonAbility;
import eu.milujukockoholky.vexliokits.abilities.list.FishermanAbility;
import eu.milujukockoholky.vexliokits.abilities.list.HadesAbility;
import eu.milujukockoholky.vexliokits.abilities.list.HulkAbility;
import eu.milujukockoholky.vexliokits.abilities.list.KangarooAbility;
import eu.milujukockoholky.vexliokits.abilities.list.MonkAbility;
import eu.milujukockoholky.vexliokits.abilities.list.PhantomAbility;
import eu.milujukockoholky.vexliokits.abilities.list.PrisonerAbility;
import eu.milujukockoholky.vexliokits.abilities.list.RiderAbility;
import eu.milujukockoholky.vexliokits.abilities.list.SouperAbility;
import eu.milujukockoholky.vexliokits.abilities.list.SpidermanAbility;
import eu.milujukockoholky.vexliokits.abilities.list.StomperAbility;
import eu.milujukockoholky.vexliokits.abilities.list.SuicidalAbility;
import eu.milujukockoholky.vexliokits.abilities.list.SummonerAbility;
import eu.milujukockoholky.vexliokits.abilities.list.SunderAbility;
import eu.milujukockoholky.vexliokits.abilities.list.SwitcherAbility;
import eu.milujukockoholky.vexliokits.abilities.list.ThorAbility;
import eu.milujukockoholky.vexliokits.abilities.list.TimelordAbility;
import eu.milujukockoholky.vexliokits.abilities.list.ViperAbility;
import eu.milujukockoholky.vexliokits.abilities.list.ZenAbility;
import eu.milujukockoholky.vexliokits.managers.PlayerDataManager;
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
      Bukkit.getPluginManager().registerEvents(new AbilityListener(VexlioKits.getInstance()), VexlioKits.getInstance());
      Bukkit.getPluginManager().registerEvents(new SpecialAbilityListener(VexlioKits.getInstance()), VexlioKits.getInstance());
   }

   public static AbilityManager getInstance() {
      return instance;
   }

   public void registerAbility(Ability var1) {
      this.abilities.put(var1.getName().toLowerCase(), var1);
   }

   public void loadAbilityConfig(Ability var1) {
      var1.load(VexlioKits.getInstance().fileManager.getConfig("abilities.yml"));
   }

   public void loadAbilitiesConfig() {
      FileConfiguration var1 = VexlioKits.getInstance().fileManager.getConfig("abilities.yml");

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
      for(Kit var2 : VexlioKits.getInstance().Kits.values()) {
         var2.loadAbilities();
      }

   }
}
