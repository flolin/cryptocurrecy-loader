package com.flolin.playground.verticles.apidataprovider;

import com.flolin.playground.domain.CurrencyItem;
import io.vertx.core.Handler;

import java.util.Optional;

/**
 * The Api Client.
 */
public interface ApiClient
{
   /**
    * Makes the actual external API call and returns a CurrencyItem.
    *
    * @param aItemKey the requested item key
    * @param aHandler a callback handler
    */
   void invokeCall(final String aItemKey, final Handler<CurrencyItem> aHandler);
}
