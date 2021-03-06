package io.dropwizard.jdbi3;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.db.TimeBoundHealthCheck;
import io.dropwizard.util.DirectExecutorService;
import io.dropwizard.util.Duration;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JdbiHealthCheck extends HealthCheck {
    private final Jdbi jdbi;
    private final String validationQuery;
    private final TimeBoundHealthCheck timeBoundHealthCheck;

    public JdbiHealthCheck(ExecutorService executorService, Duration duration, Jdbi jdbi, String validationQuery) {
        this.jdbi = jdbi;
        this.validationQuery = validationQuery;
        this.timeBoundHealthCheck = new TimeBoundHealthCheck(executorService, duration);
    }

    public JdbiHealthCheck(Jdbi jdbi, String validationQuery) {
        this(new DirectExecutorService(), Duration.seconds(0), jdbi, validationQuery);
    }

    @Override
    protected Result check() throws Exception {
        return timeBoundHealthCheck.check(() -> {
                try (Handle handle = jdbi.open()) {
                    handle.execute(validationQuery);
                    return Result.healthy();
                }
            }
        );
    }
}
