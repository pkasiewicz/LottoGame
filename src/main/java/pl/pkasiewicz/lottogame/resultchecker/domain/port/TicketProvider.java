package pl.pkasiewicz.lottogame.resultchecker.domain.port;

import pl.pkasiewicz.lottogame.resultchecker.domain.TicketData;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Port for providing ticket data by draw date.
 * Used by the result checker to retrieve tickets for comparison against winning numbers.
 */
public interface TicketProvider {
    List<TicketData> getTicketsByDrawDate(LocalDateTime drawDate);
}
