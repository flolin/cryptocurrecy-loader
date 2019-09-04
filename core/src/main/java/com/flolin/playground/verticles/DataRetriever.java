package com.flolin.playground.verticles;

import com.flolin.playground.cacheretriever.DataCache;
import com.flolin.playground.domain.CurrencyItem;
import com.flolin.playground.verticles.messages.MessageRouting;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

/**
 * Retrieves items from the cache. Cache used: https://github.com/google/guava/wiki/CachesExplained
 */
public class DataRetriever extends AbstractVerticle
{
   /**
    * The data cache
    */
   private DataCache<String, CurrencyItem> dataCache = null;

    @Override
    public void start(final Future<Void> aStartFuture) {
        vertx.eventBus().consumer(MessageRouting.RETRIEVE_ITEM_EVENT, this::consumeDataQuery);

        dataCache = new DataCache<>(this::serveDataQuery);

        aStartFuture.complete();
    }

   /**
    * returns the requested item from the cache.
    * @param aMessage the message
    */
   private void consumeDataQuery(final Message<String> aMessage)
   {
      final String reqKey = aMessage.body();
      dataCache.serve(reqKey, aMessage::reply);
   }

    /**
     * Retrieves fresh tag container data from the data store.
     *
     * @param aReqItemKey         the tag container ID
     * @param aAsyncResultHandler the async result handler
     */
    private void serveDataQuery(final String aReqItemKey, final Handler<AsyncResult<CurrencyItem>> aAsyncResultHandler) {
        vertx.eventBus()
                .send(MessageRouting.REQUEST_ITEM, aReqItemKey, (AsyncResult<Message<CurrencyItem>> asyncResult) -> {

                    if (asyncResult.succeeded()) {
                        final CurrencyItem item = asyncResult.result().body();

                        if (item == null) {
                            aAsyncResultHandler.handle(null);
                        } else {
                            aAsyncResultHandler.handle(asyncResult.map(item));
                        }
                    } else {
                        aAsyncResultHandler.handle(null);
                    }

                });
    }
}
