package com.flolin.playground.verticles.currencyloaderservice.http;

import io.netty.util.internal.StringUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class HttpRequestWrapper
{
   private final HttpServerRequest httpRequest;

   private final Long requestStartTime;

   private String requestedItem = "";

   protected final Logger log = LoggerFactory.getLogger(HttpRequestWrapper.class);

   public HttpRequestWrapper(final HttpServerRequest aHttpRequest, final Long aRequestStartTime)
   {
      httpRequest = aHttpRequest;
      requestStartTime = aRequestStartTime;
      parseParams(aHttpRequest);
   }

   protected void parseParams(final HttpServerRequest aHttpRequest)
   {
      final String item = aHttpRequest.getParam("item");

      if(!StringUtil.isNullOrEmpty(item)){
         requestedItem = item;
      }
   }

   public void dataCompleteHandler(final Handler<HttpRequestWrapper> aDataCompleteHandler)
   {
      aDataCompleteHandler.handle(this);
   }

   public HttpServerRequest getHttpRequest()
   {
      return httpRequest;
   }

   public HttpServerResponse getResponse()
   {
      return httpRequest.response();
   }

   public String getOrigin()
   {
      return httpRequest.getHeader(HttpHeaders.ORIGIN);
   }

   Optional<String> getRequestParam(final String aParamName)
   {
      try
      {
         return Optional.ofNullable(httpRequest.getParam(aParamName));
      }
      catch(final IllegalArgumentException ignored)
      {
         return Optional.empty();
      }
   }

   boolean isParamEqualTo(final String aParam, final String aValue)
   {
      final Optional<String> paramValue = getRequestParam(aParam);
      return paramValue.isPresent() && aValue.equals(paramValue.get());
   }

   public Optional<String> getRequestedItem()
   {
      return Optional.ofNullable(requestedItem);
   }

   public void setRequestedItem(final String aRequestedItem)
   {
      requestedItem = aRequestedItem;
   }
}
