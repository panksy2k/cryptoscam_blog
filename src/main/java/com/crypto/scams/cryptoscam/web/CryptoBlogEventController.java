package com.crypto.scams.cryptoscam.web;

import com.crypto.scams.cryptoscam.services.CryptoBlogEventService;
import com.crypto.scams.cryptoscam.validator.CryptoScamBlogEventValidator;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;

public class CryptoBlogEventController {

  private final CryptoBlogEventService _service;
  private final CryptoScamBlogEventValidator _validator;


  public CryptoBlogEventController(CryptoBlogEventService service) {
    this._service = service;
    this._validator = new CryptoScamBlogEventValidator();
  }

  public void createCryptoScamBlogEntry(RoutingContext context) {
    RequestBody body = context.body();
    _validator.validate(body.asJsonObject())
      .compose(_service::saveCryptoScamEvent)
      .onFailure(context::fail)
      .onSuccess(cryptoBlog -> {
        JsonObject responseBody = JsonObject.mapFrom(cryptoBlog);

        context.response().setStatusCode(201).end(responseBody.encode());
      });
  }
}
