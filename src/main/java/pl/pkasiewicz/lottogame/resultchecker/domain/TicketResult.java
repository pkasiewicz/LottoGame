package pl.pkasiewicz.lottogame.resultchecker.domain;

import lombok.Data;
import pl.pkasiewicz.lottogame.numberreceiver.domain.Ticket;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class TicketResult {

    private final TicketResultId id;
    private final UUID ticketId;
    private final Set<Integer> userNumbers;
    private final Set<Integer> hitNumbers;
    private final int hitCount;
    private final LocalDateTime drawDate;
    private final boolean isWinner;

    public static TicketResult fromTicket(Ticket ticket, Set<Integer> winningNumbers, TicketResultId ticketResultId) {
        Set<Integer> hitNumbers = ticket.getNumbers().stream()
                .filter(winningNumbers::contains)
                .collect(Collectors.toSet());

        return new TicketResult(
                ticketResultId,
                ticket.getId().value(),
                ticket.getNumbers(),
                hitNumbers,
                hitNumbers.size(),
                ticket.getDrawDate(),
                hitNumbers.size() >= 3
        );
    }
}
