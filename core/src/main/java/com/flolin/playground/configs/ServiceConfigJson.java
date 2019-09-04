package com.flolin.playground.configs;

import io.vertx.core.json.JsonObject;

public class ServiceConfigJson extends JsonObject
{
   public static final String HOST = "host";

   public static final String PORT = "port";

   public String getHost()
   {
      return getString(HOST);
   }

   public int getPort()
   {
      return getInteger(PORT);
   }

   public ServiceConfigJson(final JsonObject aJson)
   {
      super(aJson.getMap());
   }
}