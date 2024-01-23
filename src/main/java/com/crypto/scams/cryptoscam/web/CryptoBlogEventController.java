package com.crypto.scams.cryptoscam.web;

import com.crypto.scams.cryptoscam.models.CryptoScamBlogEvent;
import com.crypto.scams.cryptoscam.services.CryptoBlogEventService;
import com.crypto.scams.cryptoscam.validator.CryptoScamBlogEventValidator;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import java.util.Optional;

public class CryptoBlogEventController {

    private final CryptoBlogEventService _service;
    private final CryptoScamBlogEventValidator _validator;


    public CryptoBlogEventController(CryptoBlogEventService service) {
        this._service = service;
        this._validator = new CryptoScamBlogEventValidator();
    }

    public void createCryptoScamBlogEntry(RoutingContext context) {
        RequestBody body = context.body();
        _validator.validateCreatePayload(body.asJsonObject())
                .compose(event -> _service.saveCryptoScamEvent(event))
                .onFailure(context::fail)
                .onSuccess(cryptoBlog -> {
                    JsonObject responseBody = JsonObject.mapFrom(cryptoBlog);

                    context.response().setStatusCode(201).end(responseBody.encode());
                });
    }

    public Future<Optional<CryptoScamBlogEvent>> findCryptoScamEvent(String eventId) {
        long eventBlogId = Long.valueOf(eventId);
        return _service.findCryptoScamEvent(eventBlogId);
    }
}
