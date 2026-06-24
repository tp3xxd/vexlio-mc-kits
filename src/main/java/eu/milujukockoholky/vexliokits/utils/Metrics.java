package eu.milujukockoholky.vexliokits.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Metrics {
   private final Plugin plugin;
   private final MetricsBase metricsBase;

   public Metrics(JavaPlugin var1, int var2) {
      this.plugin = var1;
      File var3 = new File(var1.getDataFolder().getParentFile(), "bStats");
      File var4 = new File(var3, "config.yml");
      YamlConfiguration var5 = YamlConfiguration.loadConfiguration(var4);
      if (!var5.isSet("serverUuid")) {
         var5.addDefault("enabled", true);
         var5.addDefault("serverUuid", UUID.randomUUID().toString());
         var5.addDefault("logFailedRequests", false);
         var5.addDefault("logSentData", false);
         var5.addDefault("logResponseStatusText", false);
         var5.options().header("bStats (https://bStats.org) collects some basic information for plugin authors, like how\nmany people use their plugin and their total player count. It's recommended to keep bStats\nenabled, but if you're not comfortable with this, you can turn this setting off. There is no\nperformance penalty associated with having metrics enabled, and data sent to bStats is fully\nanonymous.").copyDefaults(true);

         try {
            var5.save(var4);
         } catch (IOException var11) {
         }
      }

      boolean var6 = var5.getBoolean("enabled", true);
      String var7 = var5.getString("serverUuid");
      boolean var8 = var5.getBoolean("logFailedRequests", false);
      boolean var9 = var5.getBoolean("logSentData", false);
      boolean var10 = var5.getBoolean("logResponseStatusText", false);
      Consumer<JsonObjectBuilder> var10007 = this::appendPlatformData;
      Consumer<JsonObjectBuilder> var10008 = this::appendServiceData;
      Consumer<Runnable> var10009 = (var1x) -> Bukkit.getScheduler().runTask(var1, var1x);
      Objects.requireNonNull(var1);
      this.metricsBase = new MetricsBase("bukkit", var7, var2, var6, var10007, var10008, var10009, var1::isEnabled, (var1x, var2x) -> this.plugin.getLogger().log(Level.WARNING, var1x, var2x), (var1x) -> this.plugin.getLogger().log(Level.INFO, var1x), var8, var9, var10);
   }

   public void addCustomChart(CustomChart var1) {
      this.metricsBase.addCustomChart(var1);
   }

   private void appendPlatformData(JsonObjectBuilder var1) {
      var1.appendField("playerAmount", this.getPlayerAmount());
      var1.appendField("onlineMode", Bukkit.getOnlineMode() ? 1 : 0);
      var1.appendField("bukkitVersion", Bukkit.getVersion());
      var1.appendField("bukkitName", Bukkit.getName());
      var1.appendField("javaVersion", System.getProperty("java.version"));
      var1.appendField("osName", System.getProperty("os.name"));
      var1.appendField("osArch", System.getProperty("os.arch"));
      var1.appendField("osVersion", System.getProperty("os.version"));
      var1.appendField("coreCount", Runtime.getRuntime().availableProcessors());
   }

   private void appendServiceData(JsonObjectBuilder var1) {
      var1.appendField("pluginVersion", this.plugin.getDescription().getVersion());
   }

   private int getPlayerAmount() {
      int var1 = 0;

      for(World var3 : Bukkit.getWorlds()) {
         var1 += var3.getPlayers().size();
      }

      return var1;
   }

   public static class MetricsBase {
      public static final String METRICS_VERSION = "2.2.1";
      private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, (var0) -> new Thread(var0, "bStats-Metrics"));
      private static final String REPORT_URL = "https://bStats.org/api/v2/data/%s";
      private final String platform;
      private final String serverUuid;
      private final int serviceId;
      private final Consumer<JsonObjectBuilder> appendPlatformDataConsumer;
      private final Consumer<JsonObjectBuilder> appendServiceDataConsumer;
      private final Consumer<Runnable> submitTaskConsumer;
      private final Supplier<Boolean> checkServiceEnabledSupplier;
      private final BiConsumer<String, Throwable> errorLogger;
      private final Consumer<String> infoLogger;
      private final boolean logErrors;
      private final boolean logSentData;
      private final boolean logResponseStatusText;
      private final Set<CustomChart> customCharts = new HashSet();
      private final boolean enabled;

      public MetricsBase(String var1, String var2, int var3, boolean var4, Consumer<JsonObjectBuilder> var5, Consumer<JsonObjectBuilder> var6, Consumer<Runnable> var7, Supplier<Boolean> var8, BiConsumer<String, Throwable> var9, Consumer<String> var10, boolean var11, boolean var12, boolean var13) {
         this.platform = var1;
         this.serverUuid = var2;
         this.serviceId = var3;
         this.enabled = var4;
         this.appendPlatformDataConsumer = var5;
         this.appendServiceDataConsumer = var6;
         this.submitTaskConsumer = var7;
         this.checkServiceEnabledSupplier = var8;
         this.errorLogger = var9;
         this.infoLogger = var10;
         this.logErrors = var11;
         this.logSentData = var12;
         this.logResponseStatusText = var13;
         this.checkRelocation();
         if (var4) {
            this.startSubmitting();
         }

      }

      private static byte[] compress(String var0) throws IOException {
         if (var0 == null) {
            return null;
         } else {
            ByteArrayOutputStream var1 = new ByteArrayOutputStream();
            try (GZIPOutputStream var2 = new GZIPOutputStream(var1)) {
               var2.write(var0.getBytes(StandardCharsets.UTF_8));
            }
            return var1.toByteArray();
         }
      }

      public void addCustomChart(CustomChart var1) {
         this.customCharts.add(var1);
      }

      private void startSubmitting() {
         Runnable var1 = () -> {
            if (this.enabled && (Boolean)this.checkServiceEnabledSupplier.get()) {
               if (this.submitTaskConsumer != null) {
                  this.submitTaskConsumer.accept(this::submitData);
               } else {
                  this.submitData();
               }

            } else {
               scheduler.shutdown();
            }
         };
         long var2 = (long)((double)60000.0F * ((double)3.0F + Math.random() * (double)3.0F));
         long var4 = (long)((double)60000.0F * Math.random() * (double)30.0F);
         scheduler.schedule(var1, var2, TimeUnit.MILLISECONDS);
         scheduler.scheduleAtFixedRate(var1, var2 + var4, 1800000L, TimeUnit.MILLISECONDS);
      }

      private void submitData() {
         JsonObjectBuilder var1 = new JsonObjectBuilder();
         this.appendPlatformDataConsumer.accept(var1);
         JsonObjectBuilder var2 = new JsonObjectBuilder();
         this.appendServiceDataConsumer.accept(var2);
         JsonObjectBuilder.JsonObject[] var3 = (JsonObjectBuilder.JsonObject[])this.customCharts.stream().map((var1x) -> var1x.getRequestJsonObject(this.errorLogger, this.logErrors)).filter(Objects::nonNull).toArray((var0) -> new JsonObjectBuilder.JsonObject[var0]);
         var2.appendField("id", this.serviceId);
         var2.appendField("customCharts", var3);
         var1.appendField("service", var2.build());
         var1.appendField("serverUUID", this.serverUuid);
         var1.appendField("metricsVersion", "2.2.1");
         JsonObjectBuilder.JsonObject var4 = var1.build();
         scheduler.execute(() -> {
            try {
               this.sendData(var4);
            } catch (Exception var3e) {
               if (this.logErrors) {
                  this.errorLogger.accept("Could not submit bStats metrics data", var3e);
               }
            }

         });
      }

      private void sendData(JsonObjectBuilder.JsonObject var1) throws Exception {
         if (this.logSentData) {
            this.infoLogger.accept("Sent bStats metrics data: " + var1.toString());
         }

         String var2 = String.format("https://bStats.org/api/v2/data/%s", this.platform);
         HttpsURLConnection var3 = (HttpsURLConnection)(new URL(var2)).openConnection();
         byte[] var4 = compress(var1.toString());
         var3.setRequestMethod("POST");
         var3.addRequestProperty("Accept", "application/json");
         var3.addRequestProperty("Connection", "close");
         var3.addRequestProperty("Content-Encoding", "gzip");
         var3.addRequestProperty("Content-Length", String.valueOf(var4.length));
         var3.setRequestProperty("Content-Type", "application/json");
         var3.setRequestProperty("User-Agent", "Metrics-Service/1");
         var3.setDoOutput(true);

         try (DataOutputStream var5 = new DataOutputStream(var3.getOutputStream())) {
            var5.write(var4);
         }

         StringBuilder var13 = new StringBuilder();
         try (BufferedReader var6 = new BufferedReader(new InputStreamReader(var3.getInputStream()))) {
            String var7;
            while((var7 = var6.readLine()) != null) {
               var13.append(var7);
            }
         }

         if (this.logResponseStatusText) {
            this.infoLogger.accept("Sent data to bStats and received response: " + var13);
         }

      }

      private void checkRelocation() {
         if (System.getProperty("bstats.relocatecheck") == null || !System.getProperty("bstats.relocatecheck").equals("false")) {
            String var1 = new String(new byte[]{111, 114, 103, 46, 98, 115, 116, 97, 116, 115});
            String var2 = new String(new byte[]{121, 111, 117, 114, 46, 112, 97, 99, 107, 97, 103, 101});
            if (MetricsBase.class.getPackage().getName().startsWith(var1) || MetricsBase.class.getPackage().getName().startsWith(var2)) {
               throw new IllegalStateException("bStats Metrics class has not been relocated correctly!");
            }
         }

      }
   }

   public abstract static class CustomChart {
      private final String chartId;

      protected CustomChart(String var1) {
         if (var1 == null) {
            throw new IllegalArgumentException("chartId must not be null");
         } else {
            this.chartId = var1;
         }
      }

      public JsonObjectBuilder.JsonObject getRequestJsonObject(BiConsumer<String, Throwable> var1, boolean var2) {
         JsonObjectBuilder var3 = new JsonObjectBuilder();
         var3.appendField("chartId", this.chartId);

         try {
            JsonObjectBuilder.JsonObject var4 = this.getChartData();
            if (var4 == null) {
               return null;
            }

            var3.appendField("data", var4);
         } catch (Throwable var5) {
            if (var2) {
               var1.accept("Failed to get data for custom chart with id " + this.chartId, var5);
            }

            return null;
         }

         return var3.build();
      }

      protected abstract JsonObjectBuilder.JsonObject getChartData() throws Exception;
   }

   public static class SimplePie extends CustomChart {
      private final Callable<String> callable;

      public SimplePie(String var1, Callable<String> var2) {
         super(var1);
         this.callable = var2;
      }

      protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
         String var1 = (String)this.callable.call();
         return var1 != null && !var1.isEmpty() ? (new JsonObjectBuilder()).appendField("value", var1).build() : null;
      }
   }

   public static class JsonObjectBuilder {
      private StringBuilder builder = new StringBuilder();
      private boolean hasAtLeastOneField = false;

      public JsonObjectBuilder() {
         this.builder.append("{");
      }

      private static String escape(String var0) {
         StringBuilder var1 = new StringBuilder();

         for(int var2 = 0; var2 < var0.length(); ++var2) {
            char var3 = var0.charAt(var2);
            if (var3 == '"') {
               var1.append("\\\"");
            } else if (var3 == '\\') {
               var1.append("\\\\");
            } else if (var3 <= 15) {
               var1.append("\\u000").append(Integer.toHexString(var3));
            } else if (var3 <= 31) {
               var1.append("\\u00").append(Integer.toHexString(var3));
            } else {
               var1.append(var3);
            }
         }

         return var1.toString();
      }

      public JsonObjectBuilder appendField(String var1, String var2) {
         if (var2 == null) {
            throw new IllegalArgumentException("JSON value must not be null");
         } else {
            this.appendFieldUnescaped(var1, "\"" + escape(var2) + "\"");
            return this;
         }
      }

      public JsonObjectBuilder appendField(String var1, int var2) {
         this.appendFieldUnescaped(var1, String.valueOf(var2));
         return this;
      }

      public JsonObjectBuilder appendField(String var1, JsonObject var2) {
         if (var2 == null) {
            throw new IllegalArgumentException("JSON object must not be null");
         } else {
            this.appendFieldUnescaped(var1, var2.toString());
            return this;
         }
      }

      public JsonObjectBuilder appendField(String var1, JsonObject[] var2) {
         if (var2 == null) {
            throw new IllegalArgumentException("JSON values must not be null");
         } else {
            String var3 = (String)Arrays.stream(var2).map(JsonObject::toString).collect(Collectors.joining(","));
            this.appendFieldUnescaped(var1, "[" + var3 + "]");
            return this;
         }
      }

      private void appendFieldUnescaped(String var1, String var2) {
         if (this.builder == null) {
            throw new IllegalStateException("JSON has already been built");
         } else if (var1 == null) {
            throw new IllegalArgumentException("JSON key must not be null");
         } else {
            if (this.hasAtLeastOneField) {
               this.builder.append(",");
            }

            this.builder.append("\"").append(escape(var1)).append("\":").append(var2);
            this.hasAtLeastOneField = true;
         }
      }

      public JsonObject build() {
         if (this.builder == null) {
            throw new IllegalStateException("JSON has already been built");
         } else {
            JsonObject var1 = new JsonObject(this.builder.append("}").toString());
            this.builder = null;
            return var1;
         }
      }

      public static class JsonObject {
         private final String value;

         private JsonObject(String var1) {
            this.value = var1;
         }

         public String toString() {
            return this.value;
         }
      }
   }
}
