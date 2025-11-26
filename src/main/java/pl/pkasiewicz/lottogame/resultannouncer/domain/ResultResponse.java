package pl.pkasiewicz.lottogame.resultannouncer.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class ResultResponse {

    private final ResultResponseId id;
    private final UUID ticketId;
    private final Set<Integer> userNumbers;
    private final Set<Integer> wonNumbers;
    private final Set<Integer> hitNumbers;
    private final int hitCount;
    private final LocalDateTime drawDate;
    private final boolean isWinner;
}
