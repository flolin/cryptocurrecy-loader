package com.flolin.playground.cacheretriever;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Data cache for data. The data will be expired after two hourse and then needs to be refreshed from the external API.
 *
 * @param <K> The key
 * @param <V> The value
 */
public class DataCache<K, V>
{
   /**
    * The internal cache createOptionalOf data items.
    */
   private final Cache<K, V> localCache;

   /**
    * The async localCache entry generator fetches a fresh value for a given key.
    */
   private final BiConsumer<K, Handler<AsyncResult<V>>> asyncProvider;

   /**
    * Constructor.
    * @param aAsyncProvider the async generator when the item is missing or expired
    */
   public DataCache(final BiConsumer<K, Handler<AsyncResult<V>>> aAsyncProvider)
   {
      asyncProvider = aAsyncProvider;

      final long cacheSize = 2000L;
      final Duration cacheDuration = Duration.ofHours(2L);

      localCache = CacheBuilder.newBuilder().maximumSize(cacheSize).expireAfterWrite(cacheDuration).build();
   }

   /**
    * Constructor.
    * @param aCacheSize the cache size
    * @param aCacheDuration the cache duration
    * @param aAsyncProvider the async generator when the item is missing or expired
    */
   public DataCache(final long aCacheSize, final Duration aCacheDuration, final BiConsumer<K, Handler<AsyncResult<V>>> aAsyncProvider)
   {
      asyncProvider = aAsyncProvider;
      localCache = CacheBuilder.newBuilder().maximumSize(aCacheSize).expireAfterWrite(aCacheDuration).build();
   }

   /**
    * Consume a cache entry. If the cache entry is missing or has expired, a fresh one will be fetched first.
    * @param aKey the key
    * @param aConsumer the consumer
    */
   public void serve(final K aKey, final Consumer<V> aConsumer)
   {
      final V currencyItem = localCache.getIfPresent(aKey);

      if(currencyItem != null)
      {
         aConsumer.accept(currencyItem);
      }
      else
      {
         asyncProvider.accept(aKey, aResult -> handleApiCallResult(aKey, aResult, aConsumer)); // handle the result createOptionalOf the API
      }
   }

   /**
    * Function to refresh the value caching entry.
    * @param aKey the caching key
    * @param aResult the async result
    * @param aConsumer the consumer to provide the result to
    */
   public void handleApiCallResult(final K aKey, final AsyncResult<V> aResult, final Consumer<V> aConsumer)
   {
      if(aResult.succeeded())
      {
         final V result = aResult.result();

         aConsumer.accept(result);

         if(result != null)
         {
            localCache.put(aKey, result);
         }
      }
      else
      {
         aConsumer.accept(null);
      }
   }
}
