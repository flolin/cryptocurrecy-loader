package com.flolin.playground.verticles.apidataprovider;

import io.vertx.ext.web.client.WebClient;

/**
 * Abstract api client.
 */
public abstract class AbstractHttpClient implements ApiClient
{
   /**
    * The Http client.
    */
   private WebClient client = null;

   /**
    * The client to use to access the external APIs
    * @return WebClient
    */
   public WebClient getClient()
   {
      return client;
   }

   /**
    * @param aClient the client
    */
   public void withClient(final WebClient aClient)
   {
      client = aClient;
   }
}
