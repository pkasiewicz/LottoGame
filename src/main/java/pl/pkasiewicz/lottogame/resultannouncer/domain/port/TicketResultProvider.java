package pl.pkasiewicz.lottogame.resultannouncer.domain.port;

import pl.pkasiewicz.lottogame.resultannouncer.application.ResultAnnouncerFacade;
import pl.pkasiewicz.lottogame.resultannouncer.domain.TicketResultData;

import java.util.Optional;
import java.util.UUID;

/**
 * Port for retrieving ticket result data by ticket ID.
 * Used by {@link ResultAnnouncerFacade} to check lottery results.
 */
public interface TicketResultProvider {
    Optional<TicketResultData> getResultForTicket(UUID ticketId);
}
