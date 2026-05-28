package pl.pkasiewicz.lottogame.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import pl.pkasiewicz.lottogame.numbergenerator.infrastructure.scheduler.WinningNumbersScheduler;
import pl.pkasiewicz.lottogame.numberreceiver.domain.TicketRepository;
import pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api.dto.TicketResponseDto;
import pl.pkasiewicz.lottogame.resultchecker.domain.ResultCheckerUseCase;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResult;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResultRepository;
import pl.pkasiewicz.lottogame.resultchecker.infrastructure.scheduler.ResultCheckerScheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ResultCheckerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ResultCheckerScheduler resultCheckerScheduler;

    @Autowired
    private WinningNumbersScheduler winningNumbersScheduler;

    @Autowired
    private ResultCheckerUseCase resultChecker;

    @Autowired
    private TicketResultRepository ticketResultRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        ticketResultRepository.deleteAll();
        clock.setClockToLocalDateTime(LocalDateTime.of(2025, 12, 1, 10, 0));
    }

    @Test
    void should_generate_results_for_all_tickets() throws Exception {
        // given
        mockWinningNumbers(List.of(1, 2, 3, 4, 5, 6));

        TicketResponseDto winningTicket = submitTicket(List.of(1, 2, 3, 4, 5, 6));
        TicketResponseDto losingTicket = submitTicket(List.of(7, 8, 9, 10, 11, 12));

        winningNumbersScheduler.generateWinningNumbers();
        resultCheckerScheduler.checkResults();

        // when
        TicketResult resultForWinningTicket = resultChecker.getResultForTicket(winningTicket.ticketId()).get();
        TicketResult resultForLosingTicket = resultChecker.getResultForTicket(losingTicket.ticketId()).get();

        // then
        assertAll(
                () -> assertThat(resultForWinningTicket.getId()).isNotNull(),
                () -> assertThat(resultForWinningTicket.getTicketId()).isEqualTo(winningTicket.ticketId()),
                () -> assertThat(resultForWinningTicket.getDrawDate()).isEqualTo(LocalDateTime.of(2025, 12, 6, 12, 0)),
                () -> assertThat(resultForLosingTicket.getId()).isNotNull(),
                () -> assertThat(resultForLosingTicket.getTicketId()).isEqualTo(losingTicket.ticketId()),
                () -> assertThat(resultForLosingTicket.getDrawDate()).isEqualTo(LocalDateTime.of(2025, 12, 6, 12, 0))
        );
    }

    @Test
    void should_return_empty_optional_when_ticket_result_not_found() throws Exception {
        // when && then
        assertThat(resultChecker.getResultForTicket(UUID.randomUUID())).isEmpty();
    }

    @Test
    void should_handle_multiple_draws_with_different_dates() throws Exception {
        // given
        mockWinningNumbers(List.of(1, 2, 3, 4, 5, 6));
        TicketResponseDto firstTicket = submitTicket(List.of(1, 2, 3, 4, 5, 6));
        winningNumbersScheduler.generateWinningNumbers();
        resultCheckerScheduler.checkResults();

        clock.plusDays(7);

        mockWinningNumbers(List.of(4, 5, 6, 7, 8, 9));
        TicketResponseDto secondTicket = submitTicket(List.of(1, 2, 3, 4, 5, 6));
        winningNumbersScheduler.generateWinningNumbers();
        resultCheckerScheduler.checkResults();

        // when
        TicketResult firstLotteryResult = resultChecker.getResultForTicket(firstTicket.ticketId()).get();
        TicketResult secondLotteryResult = resultChecker.getResultForTicket(secondTicket.ticketId()).get();

        // then
        assertAll(
                () -> assertThat(firstLotteryResult.getDrawDate()).isEqualTo(LocalDateTime.of(2025, 12, 6, 12, 0)),
                () -> assertThat(secondLotteryResult.getDrawDate()).isEqualTo(LocalDateTime.of(2025, 12, 13, 12, 0))
        );
    }

    @Test
    void should_not_generate_duplicate_results() throws Exception {
        // given
        mockWinningNumbers(List.of(1, 2, 3, 4, 5, 6));

        submitTicket(List.of(1, 2, 3, 4, 5, 6));

        winningNumbersScheduler.generateWinningNumbers();
        resultCheckerScheduler.checkResults();
        winningNumbersScheduler.generateWinningNumbers();
        resultCheckerScheduler.checkResults();

        // when
        List<TicketResult> allTicketResult = ticketResultRepository.findAll();

        // then
        assertThat(allTicketResult).hasSize(1);
    }
}
