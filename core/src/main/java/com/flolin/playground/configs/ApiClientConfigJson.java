package com.flolin.playground.configs;

import com.flolin.playground.verticles.ApiDataProvider;
import io.vertx.core.json.JsonObject;

/**
 * Api client config mapping to json file.
 */
public class ApiClientConfigJson extends JsonObject
{
   /**
    * Endpoint key.
    */
   public static final String ENDPOINT = "endpoint";

   /**
    * ApiKey key.
    */
   public static final String API_KEY = "apiKey";

   /**
    * Getter
    * @return the value
    */
   public String getEndpoint()
   {
      return getString(ENDPOINT);
   }

   /**
    * Getter.
    * @return the value
    */
   public String getApiKey()
   {
      return getString(API_KEY);
   }

   /**
    * Constructor.
    *
    * @param aJson a given json object.
    */
   public ApiClientConfigJson(final JsonObject aJson)
   {
      super(aJson.getMap());
   }

   /**
    * Constructor.
    *
    * @param endpoint the endpoint
    * @param apiKey the Api Key
    */
   public ApiClientConfigJson(final String endpoint, final String apiKey){
      put(ENDPOINT, endpoint);
      put(API_KEY, apiKey);
   }
}