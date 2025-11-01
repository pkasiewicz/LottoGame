package pl.pkasiewicz.lottogame.infrastructure;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class DrawDateGeneratorTest {

    @Test
    public void should_return_current_saturday_when_before_noon() {
        // given
        Clock clock = Clock.fixed(
                LocalDateTime.of(2025, 11, 1, 11, 0).toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );
        DrawDateGenerator generator = new DrawDateGenerator(clock);

        // when
        LocalDateTime result = generator.getNextDrawDate();

        // then
        LocalDateTime expectedDrawDate = LocalDateTime.of(2025, 11, 1, 12, 0);
        assertThat(result).isEqualTo(expectedDrawDate);
    }

    @Test
    public void should_return_next_saturday_when_after_noon() {
        // given
        Clock clock = Clock.fixed(
                LocalDateTime.of(2025, 11, 1, 13, 0).toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );
        DrawDateGenerator generator = new DrawDateGenerator(clock);

        // when
        LocalDateTime result = generator.getNextDrawDate();

        // then
        LocalDateTime expectedDrawDate = LocalDateTime.of(2025, 11, 8, 12, 0);
        assertThat(result).isEqualTo(expectedDrawDate);
    }

    @Test
    public void should_return_next_saturday_when_monday() {
        // given
        Clock clock = Clock.fixed(
                LocalDateTime.of(2025, 10, 27, 10, 0).toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );
        DrawDateGenerator generator = new DrawDateGenerator(clock);

        // when
        LocalDateTime result = generator.getNextDrawDate();

        // then
        LocalDateTime expectedDrawDate = LocalDateTime.of(2025, 11, 1, 12, 0);
        assertThat(result).isEqualTo(expectedDrawDate);
    }
}