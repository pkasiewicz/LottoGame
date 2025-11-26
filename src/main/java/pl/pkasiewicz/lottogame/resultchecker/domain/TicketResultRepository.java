package pl.pkasiewicz.lottogame.resultchecker.domain;

import java.util.Optional;
import java.util.UUID;

public interface TicketResultRepository {

    TicketResult save(TicketResult ticketResult);
    Optional<TicketResult> findByTicketId(UUID ticketId);
}
