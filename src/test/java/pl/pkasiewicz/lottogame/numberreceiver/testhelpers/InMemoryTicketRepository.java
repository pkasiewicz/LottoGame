package pl.pkasiewicz.lottogame.numberreceiver.testhelpers;

import pl.pkasiewicz.lottogame.numberreceiver.domain.Ticket;
import pl.pkasiewicz.lottogame.numberreceiver.domain.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTicketRepository implements TicketRepository {

    private final Map<UUID, Ticket> db = new ConcurrentHashMap<>();

    @Override
    public Ticket save(Ticket ticket) {
        db.put(ticket.getId().value(), ticket);
        return ticket;
    }

    @Override
    public List<Ticket> findAllTicketsByDrawDate(LocalDateTime drawDate) {
        return db.values()
                .stream()
                .filter(ticket -> ticket.getDrawDate().isEqual(drawDate))
                .toList();
    }

    @Override
    public Optional<Ticket> findById(UUID id) {
        return Optional.ofNullable(db.get(id));
    }

    @Override
    public List<Ticket> findAll() {
        return db.values().stream().toList();
    }

    @Override
    public void deleteAll() {
        db.clear();
    }
}
