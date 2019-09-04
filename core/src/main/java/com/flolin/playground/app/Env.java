package com.flolin.playground.app;

import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Environment configurations.
 */
@SuppressWarnings("HardcodedFileSeparator")
public enum Env
{
   /**
    * The google ligatus-drp-adexchange environment.
    */
   LIVE("app-config-prod.json", "cl.*\\.flolin.com\\.internal"),

   /**
    * The local / fallback environment.
    */
   LOCAL("app-config-local.json", null);

   /**
    * The logger.
    */
   private static final Logger LOG = LoggerFactory.getLogger(Env.class);

   /**
    * Convenient reference to the current application environment.
    */
   public static final Env CURRENT = getCurrent();

   /**
    * The environment specific config file.
    */
   private final String configFile;

   /**
    * The environment specific configuration.
    */
   private JsonObject configuration = null;

   /**
    * The host name (regex).
    */
   private final String hostName;

   /**
    * The environment configuration.
    * @param aConfigFile the config file (must be in the class path)
    * @param aHostName the host name (regex)
    */
   Env(final String aConfigFile, final String aHostName)
   {
      configFile = aConfigFile;
      hostName = aHostName;
   }

   /**
    * return the config file name.
    * @return the config file name.
    */
   public String getConfigFile()
   {
      return configFile;
   }

   /**
    * Returns the host name (regex)
    * @return the host name.
    */
   public String getHostName()
   {
      return hostName;
   }

   /**
    * Retrieve the environment specific application configuration.
    * @param aVertx the vertx instance reference
    * @return the app config
    */
   public JsonObject getAppConfig(final Vertx aVertx)
   {
      if(configuration != null)
      {
         return configuration;
      }

      try
      {
         final String environmentAppConfig = getConfigFile();
         configuration = (JsonObject)aVertx.fileSystem().readFileBlocking(environmentAppConfig).toJson();
      }
      catch(final VertxException e)
      {
         LOG.error("No valid configuration available.", e);
         throw new IllegalArgumentException("Invalid app configuration.", e);
      }

      return configuration;
   }

   /**
    * Return the environment.
    *
    * @return the environment.
    */
   private static Env getCurrent() {
      try {
         final InetAddress hostAddress = InetAddress.getLocalHost();
         final String canonicalHostName = hostAddress.getCanonicalHostName();
         return Stream.of(values())
                 .filter(aEnv -> Objects.nonNull(aEnv.getHostName()) &&
                         canonicalHostName.matches(aEnv.getHostName()))
                 .findFirst()
                 .orElse(LOCAL);
      } catch (final UnknownHostException e) {
         LOG.error("Environment identification failed.", e);
      }
      return LOCAL;
   }
}
