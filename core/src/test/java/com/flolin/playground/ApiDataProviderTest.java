package com.flolin.playground;

import com.flolin.playground.configs.ApiClientConfigJson;
import com.flolin.playground.domain.CurrencyItem;
import com.flolin.playground.verticles.ApiDataProvider;
import com.flolin.playground.verticles.messages.MessageRouting;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Tests basic functionality of the {@link ApiDataProvider} verticle class. The target API will be mocked by
 * inner class {@link ClientApiMock}, also deployed as verticle.
 */
public class ApiDataProviderTest extends AbstractVerticleTest<ApiDataProvider> {

    /**
     * The mock server port.
     */
    public static final int SERVER_PORT = 8082;

    /**
     * The constructor.
     */
    public ApiDataProviderTest() {
        super(ApiDataProvider.class);
    }

    @Before
    public void setUp(final TestContext aContext) {
        getVertx().deployVerticle(ClientApiMock.class.getName(), aContext.asyncAssertSuccess());
    }

    @Test
    public void testInvokeApiCall(final TestContext aContext) {
        final String requestItem = "ETH";

        final Async async = aContext.async();

        final String targetMockUri = String.format("http://localhost:%d/api", SERVER_PORT);

        final ApiClientConfigJson configJson = new ApiClientConfigJson(targetMockUri, "randomApiKey123456");

        deploy(configJson, aDeployed -> {
            if (aDeployed) {
                send(MessageRouting.REQUEST_ITEM, requestItem, asyncResult -> {
                    if (asyncResult.succeeded()) {
                        final CurrencyItem result = (CurrencyItem) asyncResult.result().body();

                        aContext.assertEquals(requestItem, result.getName(), "The requested item name should match");
                        aContext.assertEquals(new BigDecimal("9798.6"), result.getRateInEur(), "The rate should match");

                        async.complete();
                    } else {
                        aContext.fail();
                    }
                });
            } else {
                aContext.fail();
            }
        });
    }

    // Nested API mock server
    public static class ClientApiMock extends AbstractVerticle{

        @Override
        public void start(final Future<Void> aStartFuture) {
            getVertx().createHttpServer().requestHandler(ClientApiMock::mockResponse).listen(SERVER_PORT);
            aStartFuture.complete();
        }

        public static void mockResponse(final HttpServerRequest aServerRequest) {
            LoggerFactory.getLogger(ClientApiMock.class.getSimpleName()).info(aServerRequest.toString());
            aServerRequest.response().setStatusCode(200).end("{\"EUR\":9798.6}");
        }
    }
}

