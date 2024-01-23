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
import java.util.stream.StreamSupport;
import org.testcontainers.shaded.okio.Options;

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
      "insert into crypto_scam_event (event_id, title, description, other_reference_url, is_active, name, tags) values ($1, $2, $3, "
        + "$4, $5, $6, $7) returning event_id;";

    Tuple recordFieldsTuple =
      Tuple.of(blogEvent.getId(), blogEvent.getTitle(), blogEvent.getDescription(), blogEvent.getReference(), blogEvent.getBlogActive(),
              blogEvent.getBlogName(),
              blogEvent.getTags());

    Future<CryptoScamBlogEvent> persistedResult = this.pgClient.preparedQuery(sql)
      .execute(recordFieldsTuple)
      .compose(rows -> {
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
    String sqlUpdate = "update crypto_scam_event set title = $1, description = $2, other_reference_url = $3, is_active = $4, tags = $5 where event_id = $6";

    return pgClient.preparedQuery(sqlUpdate)
      .execute(Tuple.of(blogEvent.getTitle(), blogEvent.getDescription(), blogEvent.getReference(),
              blogEvent.getBlogActive(), blogEvent.getTags(), blogEvent.getId()))
      .flatMap(rowset -> {
          if(rowset.rowCount() == 1) {
            return Future.succeededFuture(blogEvent);
          }

          return Future.failedFuture("Update to an existing Blog Event failed!");
      });
  }

  @Override
  public Future<CryptoScamBlogEvent> removeCryptoScamEvent(long blogId) {
    return null;
  }

  @Override
  public Future<Optional<CryptoScamBlogEvent>> findCryptoScamEvent(long blogId) {
    String sql = "select * from crypto_scam_event where event_id = $1";
    CryptoBlogEventRowMapper mapper = new CryptoBlogEventRowMapper();

    return pgClient.preparedQuery(sql)
      .mapping(mapper)
      .execute(Tuple.of(blogId))
      .map(rs -> {
        if(rs.rowCount() == 0) {
            return Optional.empty();
        }

        return Optional.of(rs.iterator().next());
      });
  }

  public Future<Void> createCryptoScamBlogEventTable() {
    return this.vtx.fileSystem().readFile("sql/blog.sql")
      .map(b -> b.toString())
      .compose(query -> pgClient.query(query).execute())
      .compose(rowsAffected -> Future.succeededFuture());
  }
}
