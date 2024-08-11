package com.example.seminar11;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Timer;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class MetricsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MetricsApplication.class, args);
}

    @Bean
    public Counter requestCounter(MeterRegistry meterRegistry) {
        return Counter.builder("requests.total")
                .description("Total number of requests")
                .register(meterRegistry);
    }

    @Bean
    public Timer requestTimer(MeterRegistry meterRegistry) {
        return Timer.builder("requests.duration")
                .description("Duration of requests")
                .register(meterRegistry);
    }

    @Bean
    public Gauge activeUsersGauge(MeterRegistry meterRegistry) {
        return Gauge.builder("active.users", () -> 0)
                .description("Current number of active users")
                .register(meterRegistry);
    }
}
@RestController
class MetricsController {

    private final Counter requestCounter;
    private final Timer requestTimer;


    public MetricsController(Counter requestCounter, Timer requestTimer) {
        this.requestCounter = requestCounter;
        this.requestTimer = requestTimer;
    }


    @GetMapping("/process")
    public String processRequest() {
        requestCounter.increment();

        long startTime = System.nanoTime();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long duration = System.nanoTime() - startTime;
        requestTimer.record(duration, TimeUnit.NANOSECONDS);
        return "Request processed successfully!";
    }
}
