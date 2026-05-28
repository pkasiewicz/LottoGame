package pl.pkasiewicz.lottogame.resultchecker.domain.port;

import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResultCheckerUseCase {

    List<TicketResult> generateResults();
    Optional<TicketResult> getResultForTicket(UUID id);
}
