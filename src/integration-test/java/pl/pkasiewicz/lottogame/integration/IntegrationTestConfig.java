package pl.pkasiewicz.lottogame.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Configuration
@Profile("integration")
public class IntegrationTestConfig {

    @Bean
    @Primary
    public AdjustableClock clock() {
        return AdjustableClock.ofLocalDateAndLocalTime(
                LocalDate.of(2025, 12, 1),
                LocalTime.of(10, 0),
                ZoneId.systemDefault()
        );
    }
}
