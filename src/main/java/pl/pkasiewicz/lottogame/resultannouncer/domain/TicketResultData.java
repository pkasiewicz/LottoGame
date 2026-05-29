package pl.pkasiewicz.lottogame.resultannouncer.domain;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record TicketResultData(
        UUID id,
        UUID ticketId,
        Set<Integer>userNumbers,
        Set<Integer> wonNumbers,
        Set<Integer> hitNumbers,
        int hitCount,
        LocalDateTime drawDate,
        boolean isWinner
) {
}
