package pl.pkasiewicz.lottogame.resultannouncer.testhelpers;

import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultResponse;
import pl.pkasiewicz.lottogame.resultannouncer.domain.port.ResultResponseRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryResultResponseRepository implements ResultResponseRepository {

    private final Map<UUID, ResultResponse> db =  new ConcurrentHashMap<>();

    @Override
    public ResultResponse save(ResultResponse resultResponse) {
        db.put(resultResponse.getId().value(), resultResponse);
        return resultResponse;
    }

    @Override
    public Optional<ResultResponse> findByTicketId(UUID ticketId) {
        return db.values()
                .stream()
                .filter(resultResponse -> resultResponse.getTicketId().equals(ticketId))
                .findAny();
    }

    @Override
    public void deleteAll() {
        db.clear();
    }
}
