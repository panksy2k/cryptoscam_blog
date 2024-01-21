package com.crypto.scams.cryptoscam.repositories;

import com.crypto.scams.cryptoscam.models.CryptoScamBlogEvent;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
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

  @Test
  public void testUpdateBlogEvent(VertxTestContext testContext) {
    //Given
    Checkpoint createCP = testContext.checkpoint();
    Checkpoint updateCP = testContext.checkpoint();
    CryptoScamBlogEvent initial =
      new CryptoScamBlogEvent(1L, "Test Title", "Test Desc", "TEST REF", false, Arrays.asList("FRAUD"));

    //When
    testContext.verify(() -> {
      SUT.saveCryptoScamEvent(initial)
        .map(saved -> {
          long persistedId = saved.getId();
          Assertions.assertEquals(1L, persistedId);
          createCP.flag();
          return saved;
        })
        .map(existing -> new CryptoScamBlogEvent(existing.getId(), existing.getTitle(), existing.getDescription(),
          existing.getReference(), true, Arrays.asList("scam")))
        .compose(changedEntry -> {
          updateCP.flag();
          return SUT.updateCryptoScamEvent(changedEntry);
        })
        .map(updatedEventBlog -> updatedEventBlog.getId())
        .compose(id -> SUT.findCryptoScamEvent(id))
        .map(retrived -> {
          Assertions.assertTrue(retrived.isPresent());
          CryptoScamBlogEvent cryptoScamBlogEvent = retrived.get();
          Assertions.assertTrue(cryptoScamBlogEvent.getBlogActive());
          Assertions.assertEquals(new String[]{"scam"}, cryptoScamBlogEvent.getTags());
          return cryptoScamBlogEvent.getId();
        })
        .onSuccess(id -> {
        })
        .onFailure(id -> testContext.failNow(id));
    });

  }
}
