package pl.pkasiewicz.lottogame.resultannouncer.domain.port;

import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultResponse;

import java.util.Optional;
import java.util.UUID;

public interface ResultResponseRepository {

    ResultResponse save(ResultResponse resultResponse);
    Optional<ResultResponse> findByTicketId(UUID ticketId);
    void deleteAll();
}
