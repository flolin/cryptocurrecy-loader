package com.flolin.playground;

import com.flolin.playground.verticles.ApiDataProvider;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.junit.Test;

/**
 * Tests bootstrapping functionality of the {@link Bootstrap} verticle class.
 */
public class BootstrapTest extends AbstractVerticleTest<Bootstrap> {

    /**
     * The constructor.
     */
    public BootstrapTest() {
        super(Bootstrap.class);
    }

    @Test
    public void testBootstrap(final TestContext aContext) {
        final Async async = aContext.async();

        deploy(null, aDeployed -> {
            final WebClient client = WebClient.create(getVertx());
            client.get(8082, "127.0.0.1", "/ping").send(ar -> {
                if (ar.succeeded()) {
                    final HttpResponse<Buffer> result = ar.result();

                    aContext.assertEquals(200, result.statusCode());
                    aContext.assertEquals("OK", result.statusMessage());

                    async.complete();
                } else {
                    aContext.fail("No valid response");
                }
            });
        });
    }
}
