package com.flolin.playground;

import com.flolin.playground.app.Env;
import com.flolin.playground.verticles.ApiDataProvider;
import com.flolin.playground.verticles.DataRetriever;
import com.flolin.playground.verticles.CurrencyLoaderService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Bootstrapping all required app resources.
 */
public class Bootstrap extends AbstractVerticle
{
   /**
    * The logger.
    */
   private final Logger log = LoggerFactory.getLogger(Bootstrap.class);

   /**
    * The future of the start() method.
    */
   private Future<Void> startFuture = null;

   /**
    * Empty constructor.
    */
   public Bootstrap()
   {
   }

   @Override
   public void start(final Future<Void> aStartFuture)
   {
      startFuture = aStartFuture;

      log.info("---> App starting");

      final JsonObject appConfig = getAppConfig();
      final JsonObject globalConfig = appConfig.getJsonObject("global");

      final LocalMap<String, String> globalConfigShared = vertx.sharedData().getLocalMap("config");
      globalConfigShared.put("env", Env.CURRENT.toString());
      globalConfig.fieldNames().forEach(fieldName -> {
         final Object fieldValue = globalConfig.getValue(fieldName);
         globalConfigShared.put(fieldName, fieldValue.toString());
      });

      log.info("---> Deploying Verticals: ");

      final Map<Class<? extends Verticle>, DeploymentOptions> deployCandidates = new HashMap<>(10);

      final JsonObject serviceConfig = appConfig.getJsonObject("cryptoCurrencyLoaderService");
      final JsonObject providerConfig = appConfig.getJsonObject("apiDataProvider");

      deployCandidates.put(DataRetriever.class, new DeploymentOptions());
      deployCandidates.put(ApiDataProvider.class, new DeploymentOptions().setInstances(1).setConfig(providerConfig));
      deployCandidates.put(CurrencyLoaderService.class,
                          new DeploymentOptions().setConfig(serviceConfig));

      deployVerticles(deployCandidates, startFuture);
   }

   /**
    * Deploys verticles in the given order.
    *
    * @param aVerticleChain the verticle classes and their corresponding deployment options
    * @param aStartFuture the start future
    */
   private void deployVerticles(final Map<Class<? extends Verticle>, DeploymentOptions> aVerticleChain,
                                final Future<Void> aStartFuture)

   {
      final List<Future> deploymentFutures = aVerticleChain.entrySet()
                                                           .stream()
                                                           .map(entry -> deployVerticleInstance(entry.getKey(),
                                                                                                entry.getValue()))
                                                           .collect(Collectors.toList());

      CompositeFuture.all(deploymentFutures).setHandler(asyncResponse -> {
         if(asyncResponse.succeeded())
         {
            log.info("---> App started successfully");
            aStartFuture.complete();
         }
         else
         {
            aStartFuture.fail("Startup of verticals failed.");
         }
      });
   }

   /**
    * Deploys each given verticle with the predefined deployment option. Returns a Future to be consolidated for the
    * bootstrapping
    * @param aVerticle the given verticle
    * @param aDeploymentOptions the given deployment option
    * @return Future if the verticle was deployed
    */
   private Future<Void> deployVerticleInstance(final Class<? extends Verticle> aVerticle,
                                               final DeploymentOptions aDeploymentOptions)
   {
      final Future<Void> future = Future.future();
      vertx.deployVerticle(aVerticle, aDeploymentOptions, cb -> {
         if(cb.succeeded())
         {
            log.info(" --> " + aVerticle.getSimpleName() + " deployed");
            future.complete();
         }
         else
         {
            log.error("Failed to deploy " + aVerticle.getName() + cb.cause().getMessage());
            future.fail(cb.toString());
         }
      });
      return future;
   }

   /**
    * Retrieve the environment specific application configuration.
    * @return the app config
    */
   protected JsonObject getAppConfig()
   {
      return Env.CURRENT.getAppConfig(vertx);
   }
}
