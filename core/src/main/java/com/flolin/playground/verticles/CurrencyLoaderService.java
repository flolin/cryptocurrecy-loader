package com.flolin.playground.verticles;

import com.flolin.playground.domain.CurrencyItem;
import com.flolin.playground.verticles.currencyloaderservice.http.CryptoCurrencyGetRequest;
import com.flolin.playground.verticles.currencyloaderservice.http.HttpRequestWrapper;
import com.flolin.playground.verticles.messages.MessageRouting;
import com.flolin.playground.configs.ServiceConfigJson;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * The HTTP entrypoint of the app. Sends different messages to verticles for further processing of the request.
 */
public class CurrencyLoaderService extends AbstractVerticle {

    /**
     * The accept backlog size.
     */
    private static final int ACCEPT_BACKLOG = 16384;

    /**
     * Generic entry point.
     */
    public static final String ROOT_PATH = "/";

    /**
     * Ping resource to be used for monitoring or smoke tests.
     */
    public static final String PING_RESOURCE = "/ping";

    /**
     * Robots resource.
     */
    public static final String ROBOTS_TXT_RESOURCE = "/robots.txt";

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyLoaderService.class);

    /**
     * The HTTP server.
     */
    private HttpServer server = null;

    @Override
    public void start(final Future<Void> aStartFuture) {
        final JsonObject serviceConfig = context.config();
        final ServiceConfigJson serviceConfigJson = new ServiceConfigJson(serviceConfig);

        final Router router = Router.router(vertx);
        router.options(ROOT_PATH).handler(CurrencyLoaderService::processOptionsRequest);
        router.get(ROOT_PATH).handler(this::processGetRequest);
        router.get(PING_RESOURCE)
                .handler(
                        aRequest -> closeResponse(aRequest.response(), aRequest.request().getHeader(HttpHeaders.ORIGIN), "OK"));
        router.get(ROBOTS_TXT_RESOURCE).handler(CurrencyLoaderService::deliverRobotsTxt);
        router.get()
                .failureHandler(
                        aContext -> invalidateRequest(new HttpRequestWrapper(aContext.request(), System.currentTimeMillis()),
                                "No matching route found."));

        final HttpServerOptions options = new HttpServerOptions().setHost(serviceConfigJson.getHost())
                .setAcceptBacklog(ACCEPT_BACKLOG)
                .setCompressionSupported(true);

        server = vertx.createHttpServer(options);
        server.requestHandler(router);
        server.listen(serviceConfigJson.getPort(), serviceConfigJson.getHost(), asyncResult -> {
            String deploymentMessage = "Server listen succeeded? " + asyncResult.succeeded();
            if (!asyncResult.succeeded()) {
                deploymentMessage += "; Reason: " + asyncResult.cause();
            }
            LOGGER.info(deploymentMessage);
            aStartFuture.complete();
        });
    }

    /**
     * Wraps the request into an HttpAdxRequest and triggers further processing.
     *
     * @param aContext the routing context
     */
    private void processGetRequest(final RoutingContext aContext) {
        try {
            final HttpRequestWrapper currencyGetRequest =
                    new CryptoCurrencyGetRequest(aContext.request(), System.currentTimeMillis());
            currencyGetRequest.dataCompleteHandler(this::processRequest);
        } catch (final Throwable aThrowable) {
            LOGGER.error("AdDeliveryService processGetRequest failed. " + aThrowable);
        }
    }

    /**
     * Process the incoming request.
     *
     * @param aHttpRequest the http request
     */
    public void processRequest(final HttpRequestWrapper aHttpRequest) {
        final Optional<String> requestedItem = aHttpRequest.getRequestedItem();

        requestedItem.ifPresentOrElse(v -> sendItemRequest(v, aHttpRequest), () -> {
            final HttpServerResponse response = aHttpRequest.getResponse();
            response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
            response.end("Invalid request");
        });
    }

    /**
     * Sends the message to the cache verticle.
     *
     * @param aItem        the requested item
     * @param aHttpRequest the initial HTTP request
     */
    protected void sendItemRequest(final String aItem, final HttpRequestWrapper aHttpRequest) {
        vertx.eventBus()
                .send(MessageRouting.RETRIEVE_ITEM_EVENT, aItem, new DeliveryOptions().setSendTimeout(500L),
                        (AsyncResult<Message<CurrencyItem>> asyncResult) -> {

                            if (asyncResult.succeeded()) {

                                final Message<CurrencyItem> itemMessageResult = asyncResult.result();
                                final CurrencyItem itemResult = itemMessageResult.body();

                                if (itemResult == null) {
                                    LOGGER.info("invalid");
                                    invalidateRequest(aHttpRequest, "No result found");
                                } else {
                                    final HttpServerResponse response = aHttpRequest.getResponse();
                                    response.setStatusCode(HttpResponseStatus.OK.code());

                                    closeResponse(response, itemResult.toString());
                                }
                            } else {
                                invalidateRequest(aHttpRequest, "No result found");
                            }
                        });
    }

    /**
     * Handle PREFLIGHT requests.
     *
     * @param aContext the routing context
     */
    private static void processOptionsRequest(final RoutingContext aContext) {
        final HttpServerResponse response = aContext.response();
        response.headers()
                .set("Access-Control-Allow-Headers",
                        "Origin,Content-Type,Accept,Accept-Language,Accept-Encoding,Cache-Control,Connection,Content-Length,Cookie,Host,Pragma,Referer,User-Agent,X-Forwarded-For");
        response.headers().set("Access-Control-Allow-Methods", "GET,OPTIONS");
        closeResponse(response, aContext.request().getHeader(HttpHeaders.ORIGIN));
    }

    /**
     * Ends and closes the given response.
     *
     * @param aHttpServerResponse the response
     * @param aChunk              the response body
     */
    private static void closeResponse(final HttpServerResponse aHttpServerResponse, final String aChunk) {
        closeResponse(aHttpServerResponse, "", aChunk);
    }

    /**
     * Sets headers, ends and closes the given response.
     *
     * @param aHttpServerResponse the response
     * @param aReqOrigin          the request origin
     * @param aChunk              the string to write before ending the response
     */
    @SuppressWarnings("HardcodedFileSeparator")
    private static void closeResponse(final HttpServerResponse aHttpServerResponse, final String aReqOrigin,
                                      final String aChunk) {
        aHttpServerResponse.headers().set("Content-Type", "application/javascript; charset=UTF-8");
        aHttpServerResponse.putHeader(HttpHeaders.CONNECTION, HttpHeaders.CLOSE);

        if (aChunk != null) {
            aHttpServerResponse.end(aChunk);
        } else {
            aHttpServerResponse.end();
        }
        aHttpServerResponse.close();
    }

    /**
     * Delivers the robots.txt which forbids all crawlers.
     *
     * @param aContext the routing context
     */
    private static void deliverRobotsTxt(final RoutingContext aContext) {
        final HttpServerResponse response = aContext.response();
        response.putHeader("Content-Type", "text/plain");
        closeResponse(response, aContext.request().getHeader(HttpHeaders.ORIGIN),
                "User-agent: *" + System.lineSeparator() + "Disallow: /");
    }

    /**
     * Ends the response as a invalid request.
     *
     * @param aHttpRequest the HTTP request wrapper
     * @param aReason      the reason
     */
    private static void invalidateRequest(final HttpRequestWrapper aHttpRequest, final String aReason) {
        final HttpServerResponse response = aHttpRequest.getResponse();
        response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
        response.setStatusMessage(aReason);
        closeResponse(response, aHttpRequest.getOrigin());
    }

    @Override
    public void stop() {
        server.close();
    }
}
