package com.crypto.scams.cryptoscam.verticles;

import com.crypto.scams.cryptoscam.config.ApplicationConfig;
import com.crypto.scams.cryptoscam.config.EmbeddedApplicationConfig;
import com.crypto.scams.cryptoscam.errors.AccessDeniedException;
import com.crypto.scams.cryptoscam.errors.DependencyCreationException;
import com.crypto.scams.cryptoscam.errors.ValidationException;
import com.crypto.scams.cryptoscam.module.ApplicationModule;
import com.crypto.scams.cryptoscam.web.CryptoBlogEventController;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoScamBlogProcess extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(CryptoScamBlogProcess.class);

  private final CryptoBlogEventController _controller;

  public CryptoScamBlogProcess(CryptoBlogEventController controller) {
      this._controller = controller;
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    ApplicationConfig config = new EmbeddedApplicationConfig(vertx);
    config.receiveApplicationConfig()
      .compose(appConfig -> {
        try {
          ApplicationModule appModule = new ApplicationModule(vertx, appConfig);
          return Future.succeededFuture(appModule);
        } catch(DependencyCreationException e) {
          return Future.failedFuture(e);
        }
      })
      .map(module -> {
        Injector injector = Guice.createInjector(module);
        CryptoScamBlogProcess mainVerticle = injector.getInstance(CryptoScamBlogProcess.class);
        System.out.println("Dependency Injection Successful!");
        return mainVerticle;
      })
      .compose(vertx :: deployVerticle)
      .onFailure(errorVerticle -> {
        System.out.println(errorVerticle.getMessage());
        vertx.close();
      })
      .onSuccess(resultId -> {
        System.out.println("Process Started Successfully -- " + resultId);
      });
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    HttpServer httpServer = vertx.createHttpServer();

    Router router = Router.router(vertx);

    router.route("/api/test/hello").handler(context -> {
      String message = """
                            <h1>Hello from Vertx 4!</h1>
                            <h2>This is just a simple example, that shows how to build a
                             web application with Vertx</h2>
                            <p> :) </p>
                            """;
      context.response().putHeader("Content-Type", "text/html").setStatusCode(200).end(message);
    });
    router.route("/api/*").handler(BodyHandler.create());
    router.route("/api/*").handler(CorsHandler.create("*"));

    router.route("/api/blog/create/:blogId").handler(_controller::createCryptoScamBlogEntry);
    router.route("/api/*").failureHandler(context -> {
      if(context.failed()) {
        if(context.failed()) {
          Throwable reason = context.failure();

          if (reason instanceof ValidationException){
            ValidationException exception = (ValidationException) reason;
            JsonArray validatorMessages = exception.getMessages();
            LOG.error(validatorMessages.encode());

            context.response().setStatusCode(400).end(validatorMessages.encode());
          } else if (reason instanceof AccessDeniedException) {
            context.response().setStatusCode(403).end();
          } else {
            context.response().setStatusCode(500).end("Error happened");
          }
        }
      }
    });

    httpServer.requestHandler(router);
    httpServer.listen(8080)
        .onFailure(startPromise::fail)
        .onSuccess(h -> {
          startPromise.complete();
          LOG.info("HTTP server started on port 8080");
        });
  }
}
