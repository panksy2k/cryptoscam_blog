package com.crypto.scams.cryptoscam.repositories;

import com.crypto.scams.cryptoscam.models.CryptoScamBlogEvent;
import io.vertx.core.Future;
import java.util.Optional;

public interface CryptoScamBlogEventRepository {
    Future<CryptoScamBlogEvent> saveCryptoScamEvent(CryptoScamBlogEvent blogEvent);
    Future<CryptoScamBlogEvent> updateCryptoScamEvent(CryptoScamBlogEvent blogEvent);
    Future<CryptoScamBlogEvent> removeCryptoScamEvent(long blogId);
    Future<Optional<CryptoScamBlogEvent>> findCryptoScamEvent(long blogId);
}
