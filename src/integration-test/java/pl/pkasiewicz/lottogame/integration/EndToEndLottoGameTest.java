package pl.pkasiewicz.lottogame.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.pkasiewicz.lottogame.numbergenerator.domain.port.WinningNumbersGeneratorUseCase;
import pl.pkasiewicz.lottogame.numbergenerator.domain.exception.WinningNumbersNotFoundException;
import pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api.dto.TicketResponseDto;
import pl.pkasiewicz.lottogame.resultchecker.domain.port.ResultCheckerUseCase;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.awaitility.Awaitility.await;

class EndToEndLottoGameTest extends BaseIntegrationTest {

    @Autowired
    private WinningNumbersGeneratorUseCase winningNumbersGenerator;

    @Autowired
    private ResultCheckerUseCase resultChecker;

    @Test
    void complete_lotto_game_flow_for_winning_and_losing_ticket() throws Exception {
        // STEP 1: external service returns 6 random numbers (1, 2, 3, 4, 5, 6)
        mockWinningNumbers(List.of(1, 2, 3, 4, 5, 6));


        // STEP 2: users made POST /inputNumbers with 6 numbers (1, 2, 3, 4, 5, 6) and (7, 8, 9, 10, 11, 12) at 01-12-2025 10:00
        TicketResponseDto winningTicket = submitTicket(List.of(1, 2, 3, 4, 5, 6));
        TicketResponseDto losingTicket = submitTicket(List.of(7, 8, 9, 10, 11, 12));

        LocalDateTime drawDate = winningTicket.drawDate();


        // STEP 3: system generated winning numbers for draw date: 06.12.2025 12:00
        await().atMost(Duration.ofSeconds(20))
                .pollInterval(Duration.ofSeconds(1))
                .until(() -> {
                    try {
                        return !winningNumbersGenerator.retrieveWinningNumbersByDate(drawDate).getWinningNumbers().isEmpty();
                    } catch (WinningNumbersNotFoundException e) {
                        return false;
                    }
                });


        // STEP 4: system generated results for winning and losing ticket
        await().atMost(Duration.ofSeconds(20))
                .pollInterval(Duration.ofSeconds(1))
                .until(() -> resultChecker.getResultForTicket(winningTicket.ticketId()).isPresent());


        // STEP 5: users made GET /results/sampleTicketId and system returned 200 (OK) with correct results
        verifyResults(winningTicket, 6, "WIN_MESSAGE");
        verifyResults(losingTicket, 0, "LOSE_MESSAGE");
    }
}
