package pl.pkasiewicz.lottogame.numberreceiver.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository {

    Ticket save(Ticket ticket);
    List<Ticket> findAllTicketsByDrawDate(LocalDateTime drawDate);
    Optional<Ticket> findById(UUID id);
}
