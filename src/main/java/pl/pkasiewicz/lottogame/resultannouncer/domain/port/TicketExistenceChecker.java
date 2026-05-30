package pl.pkasiewicz.lottogame.resultannouncer.domain.port;

import java.util.UUID;

/**
 * Port for checking ticket existence by ticket ID.
 * Used by the result announcer to verify if a ticket exists before attempting to retrieve its result.
 */
public interface TicketExistenceChecker {
    boolean ticketExistsById(UUID ticketId);
}
