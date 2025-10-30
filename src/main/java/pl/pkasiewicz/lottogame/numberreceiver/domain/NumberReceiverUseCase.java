package pl.pkasiewicz.lottogame.numberreceiver.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface NumberReceiverUseCase {

    Ticket inputNumbers(Set<Integer> numbers);
    List<Ticket> retrieveAllTicketsByNextDrawDate(LocalDateTime nextDrawDate);
}
