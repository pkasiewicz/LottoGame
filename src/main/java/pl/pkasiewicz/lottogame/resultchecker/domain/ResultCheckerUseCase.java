package pl.pkasiewicz.lottogame.resultchecker.domain;

import java.util.List;
import java.util.UUID;

public interface ResultCheckerUseCase {

    List<TicketResult> generateResults();
    TicketResult getResultForTicket(UUID id);
}
