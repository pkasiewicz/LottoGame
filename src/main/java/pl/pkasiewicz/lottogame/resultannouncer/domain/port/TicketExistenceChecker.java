package pl.pkasiewicz.lottogame.resultannouncer.domain.port;

import pl.pkasiewicz.lottogame.resultannouncer.application.ResultAnnouncerFacade;

import java.util.UUID;

/**
 * Port for checking ticket existence by ticket ID.
 * Used by {@link ResultAnnouncerFacade} to verify if a ticket exists before attempting to retrieve its result.
 */
public interface TicketExistenceChecker {
    boolean ticketExistsById(UUID ticketId);
}
