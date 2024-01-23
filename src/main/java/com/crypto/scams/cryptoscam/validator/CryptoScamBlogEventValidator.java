package com.crypto.scams.cryptoscam.validator;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.ConstraintViolations;
import am.ik.yavi.core.Validator;
import com.crypto.scams.cryptoscam.errors.ValidationException;
import com.crypto.scams.cryptoscam.models.CryptoScamBlogEvent;
import io.vertx.core.Future;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CryptoScamBlogEventValidator {

    private Validator<CryptoScamBlogEvent> _validator = ValidatorBuilder.<CryptoScamBlogEvent>of()
            .constraint(CryptoScamBlogEvent::getTitle, "Title", c -> c.notBlank())
            .constraint(CryptoScamBlogEvent::getDescription, "Description", c -> c.notBlank())
            .constraint(CryptoScamBlogEvent::getBlogName, "Blog Name", c -> c.notBlank())
            .build();

    public Future<CryptoScamBlogEvent> validateCreatePayload(JsonObject payloadCreateBlog) {
        CryptoScamBlogEvent cryptoScamBlogEvent;

        try {
            cryptoScamBlogEvent = Json.decodeValue(payloadCreateBlog.toBuffer(), CryptoScamBlogEvent.class);
        } catch (DecodeException e) {
            JsonObject errorObj = new JsonObject();
            errorObj.put("message", "Invalid message payload -- cannot decode the payload");

            JsonArray errorMessage = JsonArray.of(errorObj);
            return Future.failedFuture(new ValidationException(errorMessage));
        }

        ConstraintViolations validateResult = _validator.validate(cryptoScamBlogEvent);
        if (!validateResult.isValid()) {
            List<JsonObject> constraintVoilationList = validateResult.stream()
                    .map(cv -> JsonObject.of("Field Name", cv.name(), "Error Message", cv.message()))
                    .collect(Collectors.toList());

            return Future.failedFuture(new ValidationException(JsonArray.of(constraintVoilationList)));
        }

        return Future.succeededFuture(cryptoScamBlogEvent);
    }

    public Future<Long> validateEventID(long eventID) {
      if(eventID < 1) {
        return Future.failedFuture(new ValidationException(JsonArray.of("event id should be a positive whole number")));
      }

      return Future.succeededFuture(eventID);
    }
}
