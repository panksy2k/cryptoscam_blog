package com.crypto.scams.cryptoscam.module;

import com.crypto.scams.cryptoscam.errors.DependencyCreationException;
import com.crypto.scams.cryptoscam.models.ApplicationConfiguration;
import com.crypto.scams.cryptoscam.repositories.CryptoScamBlogEventRepositoryImpl;
import com.crypto.scams.cryptoscam.services.CryptoBlogEventService;
import com.crypto.scams.cryptoscam.services.CryptoBlogEventServiceImpl;
import com.crypto.scams.cryptoscam.verticles.CryptoScamBlogProcess;
import com.crypto.scams.cryptoscam.web.CryptoBlogEventController;
import com.google.inject.AbstractModule;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationModule.class);

    private final CryptoScamBlogEventRepositoryImpl _repository;
    private final CryptoBlogEventService _service;
    private final CryptoBlogEventController _controller;

    public ApplicationModule(Vertx vertx, ApplicationConfiguration envAppConfig) throws DependencyCreationException {
      SqlClient dbClient = PgPool.client(vertx, envAppConfig.getDatabaseUrl());
      _repository = new CryptoScamBlogEventRepositoryImpl(vertx, dbClient);
      _repository.createCryptoScamBlogEventTable("sql/blog.sql")
        .onSuccess(h -> System.out.println("Created Table!"))
        .onFailure(h -> System.out.println("Error whilst creating Table!"));
      _service = new CryptoBlogEventServiceImpl(_repository);
      _controller = new CryptoBlogEventController(_service);
    }

  @Override
  protected void configure() {
    bind(CryptoScamBlogProcess.class).toInstance(new CryptoScamBlogProcess(_controller));
  }
}
