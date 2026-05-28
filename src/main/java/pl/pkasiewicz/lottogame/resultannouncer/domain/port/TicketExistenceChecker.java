package pl.pkasiewicz.lottogame.resultannouncer.domain.port;

import java.util.UUID;

public interface TicketExistenceChecker {
    boolean ticketExistsById(UUID ticketId);
}
