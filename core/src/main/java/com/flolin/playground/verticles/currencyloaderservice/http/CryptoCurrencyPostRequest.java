package com.flolin.playground.verticles.currencyloaderservice.http;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;

/**
 * Post Wrapper of the HttpRequestWrapper
 */
public class CryptoCurrencyPostRequest extends HttpRequestWrapper
{
   /**
    * POST Constructor.
    * @param aHttpRequest the actual http request
    * @param aRequestStartTime a start time in ms.
    */
   public CryptoCurrencyPostRequest(final HttpServerRequest aHttpRequest, final Long aRequestStartTime)
   {
      super(aHttpRequest, aRequestStartTime);
   }

   @Override
   public void dataCompleteHandler(final Handler<HttpRequestWrapper> aDataCompleteHandler)
   {
      getHttpRequest().bodyHandler(aTotalBuffer -> {
         final String payload = aTotalBuffer.toString();
         final PostPayload postPayload = decodePayload(payload);
         // setBody data, decode or something
      });
      aDataCompleteHandler.handle(this);
   }

   /**
    * Decodes the JSON body String and creates a JSON representation.
    *
    * @param aPayload the json string
    * @return a mapped JSON object
    */
   PostPayload decodePayload(final String aPayload)
   {
      PostPayload result = null;
      try
      {
         result = Json.decodeValue(aPayload, PostPayload.class);
      }
      catch(final DecodeException aEx)
      {
         log.error("Payload not parsable. '" + aPayload + '\'', aEx.getMessage());
      }
      return result;
   }
}
