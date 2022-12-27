package com.github.flx5.zonky.postgres.autoconfiguration;

import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.stereotype.Component;

@Component
public class MyBuilderCustomizer implements ConnectionFactoryOptionsBuilderCustomizer {

    private boolean executed;

    @Override
    public void customize(ConnectionFactoryOptions.Builder builder) {
        executed = true;
    }

    public boolean isExecuted() {
        return executed;
    }
}
