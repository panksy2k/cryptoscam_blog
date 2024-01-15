package com.crypto.scams.cryptoscam.repositories;

import com.crypto.scams.cryptoscam.models.CryptoScamBlogEvent;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import java.util.Optional;

public class CryptoScamBlogEventRepositoryImpl implements CryptoScamBlogEventRepository {

  private final Vertx vtx;
  private final SqlClient pgClient;

  public CryptoScamBlogEventRepositoryImpl(Vertx vertx, SqlClient client) {
    this.vtx = vertx;
    this.pgClient = client;
  }

  @Override
  public Future<CryptoScamBlogEvent> saveCryptoScamEvent(CryptoScamBlogEvent blogEvent) {
    String sql =
      "insert into crypto_scam_event (title, description, other_reference_url, is_active, tags) values ($1, $2, $3, "
        + "$4, $5) returning event_id;";

    Tuple recordFieldsTuple =
      Tuple.of(blogEvent.getTitle(), blogEvent.getDescription(), blogEvent.getReference(), blogEvent.getBlogActive(),
        blogEvent.getTags());

    Future<CryptoScamBlogEvent> persistedResult = this.pgClient.preparedQuery(sql)
      .execute(recordFieldsTuple)
      .flatMap(rows -> {
        if (rows.rowCount() == 0) {
          return Future.failedFuture("Cannot insert the crypto blog event");
        }

        RowIterator<Row> rwIterator = rows.iterator();
        if (rwIterator.hasNext()) {
          Row rw = rwIterator.next();
          Long eventId = rw.getLong("event_id");
          return Future.succeededFuture(CryptoScamBlogEvent.createPersistedRecord(eventId, blogEvent));
        }

        return Future.failedFuture("Cannot insert the crypto blog event");
      });

    return persistedResult;
  }

  @Override
  public Future<CryptoScamBlogEvent> updateCryptoScamEvent(CryptoScamBlogEvent blogEvent) {
    return null;
  }

  @Override
  public Future<CryptoScamBlogEvent> removeCryptoScamEvent(long blogId) {
    return null;
  }

  @Override
  public Future<Optional<CryptoScamBlogEvent>> findCryptoScamEvent(long blogId) {
    return null;
  }

  public Future<Void> createCryptoScamBlogEventTable(String sqlScript) {
    return this.vtx.fileSystem().readFile(sqlScript)
      .map(b -> b.toString())
      .compose(query -> pgClient.query(query).execute())
      .compose(rowsAffected -> Future.succeededFuture());
  }
}
