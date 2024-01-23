package com.crypto.scams.cryptoscam.services;

import com.crypto.scams.cryptoscam.models.CryptoScamBlogEvent;
import com.crypto.scams.cryptoscam.repositories.CryptoScamBlogEventRepository;
import io.vertx.core.Future;
import java.util.Optional;

public class CryptoBlogEventServiceImpl implements CryptoBlogEventService {
    private final CryptoScamBlogEventRepository _repository;

    public CryptoBlogEventServiceImpl(CryptoScamBlogEventRepository repository) {
        this._repository = repository;
    }

    @Override
    public Future<CryptoScamBlogEvent> saveCryptoScamEvent(CryptoScamBlogEvent blogEvent) {
        return _repository.saveCryptoScamEvent(blogEvent);
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
        return _repository.findCryptoScamEvent(blogId);
    }
}
