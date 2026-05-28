package pl.pkasiewicz.lottogame.resultchecker.domain.port;

import pl.pkasiewicz.lottogame.resultchecker.domain.TicketData;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketProvider {
    List<TicketData> getTicketsByDrawDate(LocalDateTime drawDate);
}
