package pl.pkasiewicz.lottogame.resultchecker.domain.port;

import pl.pkasiewicz.lottogame.resultchecker.application.ResultCheckerFacade;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketData;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for providing ticket data based on draw date.
 * This is used by the {@link ResultCheckerFacade} to retrieve tickets that need to be checked against the winning numbers for a specific draw date.
 */
public interface TicketProvider {
    List<TicketData> getTicketsByDrawDate(LocalDateTime drawDate);
}
