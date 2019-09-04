package com.flolin.playground;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.function.Consumer;

/**
 * An abstract class with common unit testing functionality of verticles.
 */
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
@RunWith(VertxUnitRunner.class)
public abstract class AbstractVerticleTest<T extends Verticle>
{
   private final Class<? extends Verticle> type;

   @SuppressWarnings("Guava")
   private static final Supplier<Vertx> VERTX_SUPPLIER = Suppliers.memoize(() ->
   {
      final Vertx realVertx = Vertx.vertx();
      return Mockito.spy(realVertx);
   });

   private String deploymentId = null;

   /**
    * The constructor.
    */
   protected AbstractVerticleTest(final Class<T> aType)
   {
      type = aType;
   }

   public void deploy(final JsonObject aConfig, final Consumer<Boolean> aRunOnDeployment)
   {
      final Vertx vertx = getVertx();
      vertx.deployVerticle(type.getName(), new DeploymentOptions().setConfig(aConfig), aAsyncResult ->
      {
         if(aAsyncResult.succeeded())
         {
            deploymentId = aAsyncResult.result();
            aRunOnDeployment.accept(true);
         }
         else
         {
            aRunOnDeployment.accept(false);
         }
      });
   }

   public void send(final String aAddress, final Object aMessage)
   {
      getVertx().eventBus().send(aAddress, aMessage);
   }

   public <P> void send(final String aAddress, final Object aMessage,
                        final Handler<AsyncResult<Message<P>>> aReplyHandler)
   {
      getVertx().eventBus().send(aAddress, aMessage, aReplyHandler);
   }

   public void send(final String aAddress, final Object aMessage, final DeliveryOptions aDeliveryOptions)
   {
      getVertx().eventBus().send(aAddress, aMessage, aDeliveryOptions);
   }

   protected static Vertx getVertx()
   {
      return VERTX_SUPPLIER.get();
   }

   @SuppressWarnings("BeforeOrAfterWithIncorrectSignature")
   @After
   public void tearDown(final TestContext aContext)
   {
      if(null != deploymentId)
      {
         final Async async = aContext.async();

         getVertx().undeploy(deploymentId, aAsyncResult -> async.complete());
      }
   }
}
