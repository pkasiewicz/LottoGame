package pl.pkasiewicz.lottogame.resultannouncer.infrastructure.api.dto;

import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultResponse;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record ResultDto(
        UUID ticketId,
        Set<Integer> userNumbers,
        Set<Integer> wonNumbers,
        Set<Integer> hitNumbers,
        int hitCount,
        LocalDateTime drawDate,
        boolean isWinner
) {
    public static ResultDto from(ResultResponse response) {
        return new ResultDto(
                response.getTicketId(),
                response.getUserNumbers(),
                response.getWonNumbers(),
                response.getHitNumbers(),
                response.getHitCount(),
                response.getDrawDate(),
                response.isWinner()
        );
    }
}
