package com.crypto.scams.cryptoscam.repositories;

import com.crypto.scams.cryptoscam.models.CryptoScamBlogEvent;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlClient;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(VertxExtension.class)
public class CryptoScamBlogEventRepositoryImplTest {

  private CryptoScamBlogEventRepositoryImpl SUT;

  @Container
  private PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:11-alpine")
    .withDatabaseName("cryptoscamdb").withUsername("pankaj").withPassword("secret");

  @BeforeEach
  void setup (Vertx vertx, VertxTestContext context) {
    int port = container.getFirstMappedPort();
    String uri = "postgresql://pankaj:secret@localhost:" + port + "/cryptoscamdb";
    SqlClient client = PgPool.client(vertx, uri);
    SUT = new CryptoScamBlogEventRepositoryImpl(vertx, client);
    SUT.createCryptoScamBlogEventTable().onSuccess(r -> context.completeNow()).onFailure(context::failNow);
  }

  @Test
  public void testContainerIsRunningTest(){
    Assertions.assertTrue(container.isRunning());
  }

  @Test
  public void testCreateCryptoScamEvent(VertxTestContext context) {
    CryptoScamBlogEvent data = new CryptoScamBlogEvent(2L, "kucisev.vip scam", "There is this gang of people...", "http://somewebsite.co.uk", true,
      Arrays.asList("kucoin", "kucisev", "crypto", "scam", "exchangescam"));

    context.verify(() -> {
        Future<CryptoScamBlogEvent> result = SUT.saveCryptoScamEvent(data);
        result.onFailure(context::failNow);
        result.onSuccess(blogEvent -> {
            Assertions.assertEquals(2L, blogEvent.getId());
            context.completeNow();
        });
    });
  }
}
