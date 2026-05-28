package pl.pkasiewicz.lottogame.numberreceiver.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface NumberReceiverUseCase {

    Ticket inputNumbers(Set<Integer> numbers);
    List<Ticket> retrieveAllTicketsByNextDrawDate(LocalDateTime nextDrawDate);
    boolean ticketExists(UUID ticketId);
}
