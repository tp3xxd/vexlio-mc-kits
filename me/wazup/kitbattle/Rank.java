package me.wazup.kitbattle;

import java.util.ArrayList;
import java.util.List;

public class Rank {
   public static ArrayList<Rank> orderd = new ArrayList();
   private final int RequiredExp;
   private final List<String> excutedCommands;
   private final String name;
   private final String prefix;

   public Rank(String var1, String var2, int var3, List<String> var4) {
      this.RequiredExp = var3;
      this.excutedCommands = var4;
      this.name = var1;
      this.prefix = var2;
      orderd.add(this);
   }

   public int getRequiredExp() {
      return this.RequiredExp;
   }

   public List<String> getExcutedCommands() {
      return this.excutedCommands;
   }

   public String getName() {
      return this.name;
   }

   public String getPrefix() {
      return this.prefix;
   }
}
