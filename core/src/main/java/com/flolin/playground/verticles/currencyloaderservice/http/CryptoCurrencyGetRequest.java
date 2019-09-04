package com.flolin.playground.verticles.currencyloaderservice.http;

import io.vertx.core.http.HttpServerRequest;

/**
 *  GET Wrapper of the HttpRequestWrapper
 */
public class CryptoCurrencyGetRequest extends HttpRequestWrapper
{
   /**
    * GET Constructor.
    *
    * @param aHttpRequest the actual http request
    * @param aRequestStartTime a start time in ms.
    */
   public CryptoCurrencyGetRequest(final HttpServerRequest aHttpRequest, final Long aRequestStartTime)
   {
      super(aHttpRequest, aRequestStartTime);
   }
}
