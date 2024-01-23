package com.crypto.scams.cryptoscam.repositories;

import com.crypto.scams.cryptoscam.models.CryptoScamBlogEvent;
import io.vertx.sqlclient.Row;
import java.util.Arrays;
import java.util.function.Function;

public class CryptoBlogEventRowMapper implements Function<Row, CryptoScamBlogEvent> {

  @Override
  public CryptoScamBlogEvent apply(Row row) {
    return new CryptoScamBlogEvent(row.getLong("event_id"),
        row.getString("title"),
        row.getString("description"),
        row.getString("other_reference_url"),
        row.getBoolean("is_active"),
        row.getString("name"),
        Arrays.asList(row.getArrayOfStrings("tags")));
  }
}
