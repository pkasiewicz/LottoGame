package pl.pkasiewicz.lottogame.resultchecker.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResultCheckerUseCase {

    List<TicketResult> generateResults();
    Optional<TicketResult> getResultForTicket(UUID id);
}
