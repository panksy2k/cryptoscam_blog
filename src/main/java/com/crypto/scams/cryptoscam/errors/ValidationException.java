package com.crypto.scams.cryptoscam.errors;

import io.vertx.core.json.JsonArray;

public class ValidationException extends RuntimeException {

  private final JsonArray _messages;

  public ValidationException(JsonArray messages) {
    this._messages = messages;
  }

  @Override
  public String getMessage() {
    StringBuilder sb = new StringBuilder();
    _messages.stream().map(m -> m.toString()).forEach(ms -> sb.append(ms));
    return sb.toString();
  }

  public JsonArray getMessages() {
    return _messages;
  }
}
