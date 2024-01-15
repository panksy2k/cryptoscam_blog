package com.crypto.scams.cryptoscam.config;

import com.crypto.scams.cryptoscam.models.ApplicationConfiguration;
import io.vertx.core.Future;

public interface ApplicationConfig {
    Future<ApplicationConfiguration> receiveApplicationConfig();
}
