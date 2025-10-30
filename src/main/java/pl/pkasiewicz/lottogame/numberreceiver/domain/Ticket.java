package pl.pkasiewicz.lottogame.numberreceiver.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class Ticket {

    private final TicketId id;
    private final Set<Integer> numbers;
    private final LocalDateTime drawDate;
}
