package pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api.dto;

import pl.pkasiewicz.lottogame.numberreceiver.domain.Ticket;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record TicketResponseDto(
        UUID ticketId,
        Set<Integer> numbers,
        LocalDateTime drawDate
) {
    public static TicketResponseDto from(Ticket ticket) {
        return new TicketResponseDto(
                ticket.getId().value(),
                ticket.getNumbers(),
                ticket.getDrawDate()
        );
    }
}
