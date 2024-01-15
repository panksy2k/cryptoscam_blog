package com.crypto.scams.cryptoscam.services;

import com.crypto.scams.cryptoscam.models.CryptoScamBlogEvent;
import io.vertx.core.Future;
import java.util.Optional;

public interface CryptoBlogEventService {
  Future<CryptoScamBlogEvent> saveCryptoScamEvent(CryptoScamBlogEvent blogEvent);
  Future<CryptoScamBlogEvent> updateCryptoScamEvent(CryptoScamBlogEvent blogEvent);
  Future<CryptoScamBlogEvent> removeCryptoScamEvent(long blogId);
  Future<Optional<CryptoScamBlogEvent>> findCryptoScamEvent(long blogId);
}
