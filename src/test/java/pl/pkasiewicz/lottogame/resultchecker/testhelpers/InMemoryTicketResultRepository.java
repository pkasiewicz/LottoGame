package pl.pkasiewicz.lottogame.resultchecker.testhelpers;

import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResult;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResultRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTicketResultRepository implements TicketResultRepository {

    private final Map<UUID, TicketResult> db =  new ConcurrentHashMap<>();

    @Override
    public TicketResult save(TicketResult ticketResult) {
        db.put(ticketResult.getId().value(), ticketResult);
        return ticketResult;
    }

    @Override
    public Optional<TicketResult> findByTicketId(UUID ticketId) {
        return db.values()
                .stream()
                .filter(ticketResult -> ticketResult.getTicketId().equals(ticketId))
                .findAny();
    }

    public List<TicketResult> findAll() {
        return new ArrayList<>(db.values());
    }
}
