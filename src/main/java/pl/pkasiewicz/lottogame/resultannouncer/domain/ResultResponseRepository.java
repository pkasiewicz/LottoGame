package pl.pkasiewicz.lottogame.resultannouncer.domain;

import java.util.Optional;
import java.util.UUID;

public interface ResultResponseRepository {

    ResultResponse save(ResultResponse resultResponse);
    Optional<ResultResponse> findByTicketId(UUID ticketId);
}
