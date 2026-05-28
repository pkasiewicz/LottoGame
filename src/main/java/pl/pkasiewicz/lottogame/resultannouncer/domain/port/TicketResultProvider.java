package pl.pkasiewicz.lottogame.resultannouncer.domain.port;

import pl.pkasiewicz.lottogame.resultannouncer.domain.TicketResultData;

import java.util.Optional;
import java.util.UUID;

public interface TicketResultProvider {
    Optional<TicketResultData> getResultForTicket(UUID ticketId);
}
