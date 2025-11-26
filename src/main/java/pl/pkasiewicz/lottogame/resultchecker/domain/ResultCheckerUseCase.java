package pl.pkasiewicz.lottogame.resultchecker.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResultCheckerUseCase {

    List<TicketResult> generateResults();
    TicketResult getResultForTicket(UUID id);
    Optional<LocalDateTime> getDrawDateForTicket(UUID ticketId);
}
