package pl.pkasiewicz.lottogame.infrastructure;

import org.springframework.stereotype.Component;
import pl.pkasiewicz.lottogame.domain.DrawDateGenerable;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

@Component
public class DrawDateGenerator implements DrawDateGenerable {

    private static final LocalTime DRAW_TIME = LocalTime.of(12,0, 0);
    private static final TemporalAdjuster NEXT_DRAW_TIME = TemporalAdjusters.next(DayOfWeek.SATURDAY);
    private final Clock clock;

    public DrawDateGenerator(Clock clock) {
        this.clock = clock;
    }

    @Override
    public LocalDateTime getNextDrawDate() {
        LocalDateTime currentDateTime = LocalDateTime.now(clock);
        if (isSaturdayAndBeforeNoon(currentDateTime)) {
            return LocalDateTime.of(currentDateTime.toLocalDate(), DRAW_TIME);
        }
        LocalDateTime drawDate = currentDateTime.with(NEXT_DRAW_TIME);
        return LocalDateTime.of(drawDate.toLocalDate(), DRAW_TIME);
    }

    private boolean isSaturdayAndBeforeNoon(LocalDateTime currentDateTime) {
        return currentDateTime.getDayOfWeek().equals(DayOfWeek.SATURDAY) && currentDateTime.toLocalTime().isBefore(DRAW_TIME);
    }
}
