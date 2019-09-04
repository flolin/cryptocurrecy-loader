package com.flolin.playground.verticles.apidataprovider.cryptocompare;

import com.flolin.playground.configs.ApiClientConfigJson;
import com.flolin.playground.domain.CurrencyItem;
import com.flolin.playground.verticles.apidataprovider.AbstractHttpClient;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.time.LocalDateTime;

/**
 * Concrete CryptoCompare client.
 */
public class CryptoCompareApiClient extends AbstractHttpClient
{
   /**
    * The crypto compare endpoint.
    */
   private final String uri;

   /**
    * The crypto compare apiKey header value.
    */
   private final String apiKeyHeaderValue;

   /**
    * Constructor.
    *
    * @param aVertx a Vertx instance
    * @param aApiConfigJson the config json
    */
   public CryptoCompareApiClient(final Vertx aVertx, final ApiClientConfigJson aApiConfigJson)
   {
      final WebClientOptions options = new WebClientOptions().setConnectTimeout(300);
      withClient(WebClient.create(aVertx, options));

      uri = aApiConfigJson.getEndpoint();
      apiKeyHeaderValue = String.format("ApiKey %s", aApiConfigJson.getApiKey());
   }

   @Override
   public void invokeCall(final String aItemKey, final Handler<CurrencyItem> aHandler) {

      if (uri.isEmpty() || apiKeyHeaderValue.isEmpty()) {
         aHandler.handle(null);
      }

      final String targetUri = uri.replaceFirst("\\{TARGET_CUR}", aItemKey);

      getClient().getAbs(targetUri)
              .putHeader(HttpHeaders.AUTHORIZATION.toString(), apiKeyHeaderValue)
              .send(asyncResult -> {
                 if (asyncResult.succeeded()) {
                    final HttpResponse<Buffer> result = asyncResult.result();
                    final Buffer body = result.body();

                    if (null == body) {
                       aHandler.handle(null);
                    } else {
                       final CryptoCompareApiResponse response = new CryptoCompareApiResponse(body.toJsonObject());
                       aHandler.handle(new CurrencyItem(aItemKey, response.getRate(), LocalDateTime.now()));
                    }
                 } else {
                    aHandler.handle(null);
                 }
              });
   }
}
