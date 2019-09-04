package com.flolin.playground.verticles;

import com.flolin.playground.verticles.apidataprovider.ApiClient;
import com.flolin.playground.verticles.apidataprovider.cryptocompare.CryptoCompareApiClient;
import com.flolin.playground.configs.ApiClientConfigJson;
import com.flolin.playground.verticles.messages.MessageRouting;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Verticle to invoke the API call.
 */
public class ApiDataProvider extends AbstractVerticle
{
   /**
    * The logger.
    */
   private final Logger log = LoggerFactory.getLogger(ApiDataProvider.class);

   /**
    * The client to use to access the external APIs
    */
   private ApiClient client = null;

   /**
    * Constructor.
    *
    * @param aStartFuture the start future
    */
   @Override
   public void start(final Future<Void> aStartFuture)
   {
      final JsonObject providerConfig = context.config();
      final ApiClientConfigJson apiConfigJson = new ApiClientConfigJson(providerConfig);

      client = getClient(apiConfigJson);

      vertx.eventBus().consumer(MessageRouting.REQUEST_ITEM, this::callApi);

      aStartFuture.complete();
   }

   /**
    * Returns an API client instance.
    *
    * @param apiConfigJson the client config
    * @return a cliet instance
    */
   protected CryptoCompareApiClient getClient(final ApiClientConfigJson apiConfigJson) {
      return new CryptoCompareApiClient(vertx, apiConfigJson);
   }

   /**
    * Sends request to the external API.
    * @param aMessage the message with the requested key item.
    */
   private void callApi(final Message<String> aMessage)
   {
      final String itemKey = aMessage.body();

      if(itemKey == null)
      {
         aMessage.reply(null);
         return;
      }

      client.invokeCall(itemKey, aMessage::reply);
   }
}

