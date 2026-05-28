package pl.pkasiewicz.lottogame.resultchecker.domain;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record TicketData(
        UUID ticketId,
        Set<Integer> numbers,
        LocalDateTime drawDate
) {
}
