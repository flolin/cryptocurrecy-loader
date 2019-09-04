package com.flolin.playground.app;

import io.vertx.core.dns.AddressResolverOptions;

import java.util.Arrays;
import java.util.List;

/**
 * Custom address resolver factory to add Google DNS resolvers.
 */
public class AddressResolverOptionsFactory
{

   /**
    * Max cache ttl for dns entries in seconds.
    */
   private static final int MAX_TTL_IN_SECONDS = 90;

   /**
    * List createOptionalOf Google hosted DNS servers.
    */
   private static final List<String> DNS_SERVERS = Arrays.asList("8.8.8.8", "8.8.4.4");

   /**
    * Empty constructor
    */
   private AddressResolverOptionsFactory()
   {
   }

   /**
    * Creates the specific config.
    * @return the address resolver config
    */
   public static AddressResolverOptions create()
   {
      final AddressResolverOptions addressResolverOptions = new AddressResolverOptions();

      DNS_SERVERS.forEach(addressResolverOptions::addServer);
      addressResolverOptions.setCacheMaxTimeToLive(MAX_TTL_IN_SECONDS);

      return addressResolverOptions;
   }
}
