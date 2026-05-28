package pl.pkasiewicz.lottogame.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.pkasiewicz.lottogame.numbergenerator.infrastructure.scheduler.WinningNumbersScheduler;
import pl.pkasiewicz.lottogame.numberreceiver.domain.TicketRepository;
import pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api.dto.TicketResponseDto;
import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultResponseRepository;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResultRepository;
import pl.pkasiewicz.lottogame.resultchecker.infrastructure.scheduler.ResultCheckerScheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ResultAnnouncerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ResultResponseRepository resultResponseRepository;

    @Autowired
    private TicketResultRepository ticketResultRepository;

    @Autowired
    private ResultCheckerScheduler resultCheckerScheduler;

    @Autowired
    private WinningNumbersScheduler winningNumbersScheduler;

    @BeforeEach
    void setUp() {
        resultResponseRepository.deleteAll();
        ticketRepository.deleteAll();
        ticketResultRepository.deleteAll();
        clock.setClockToLocalDateTime(LocalDateTime.of(2025, 12, 1, 10, 0));
    }

    @Test
    void should_return_ticket_not_found_when_ticket_does_not_exist() throws Exception {
        // given
        UUID nonExistingTicketId = UUID.randomUUID();

        // when && then
        mockMvc.perform(get("/api/results/{ticketId}", nonExistingTicketId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("TICKET_NOT_FOUND"))
                .andExpect(jsonPath("$.result").doesNotExist());
    }

    @Test
    void should_return_waiting_for_draw_when_draw_has_not_occurred() throws Exception {
        // given
        TicketResponseDto ticket = submitTicket(List.of(1, 2, 3, 4, 5, 6));

        // when
        mockMvc.perform(get("/api/results/{ticketId}", ticket.ticketId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING_FOR_DRAW"))
                .andExpect(jsonPath("$.result").doesNotExist());

    }

    @Test
    void should_return_win_message_when_ticket_wins() throws Exception {
        // given
        mockWinningNumbers(List.of(1, 2, 3, 4, 5, 6));
        TicketResponseDto ticket = submitTicket(List.of(1, 2, 3, 4, 5, 6));

        winningNumbersScheduler.generateWinningNumbers();
        resultCheckerScheduler.checkResults();

        // when && then
        verifyResults(ticket, 6, "WIN_MESSAGE");
    }

    @Test
    void should_return_lose_message_when_ticket_loses() throws Exception {
        // given
        mockWinningNumbers(List.of(1, 2, 3, 4, 5, 6));
        TicketResponseDto ticket = submitTicket(List.of(7, 8, 9, 10, 11, 12));

        winningNumbersScheduler.generateWinningNumbers();
        resultCheckerScheduler.checkResults();

        // when && then
        verifyResults(ticket, 0, "LOSE_MESSAGE");
    }

    @Test
    void should_return_already_checked_when_result_was_previously_retrieved() throws Exception {
        // given
        mockWinningNumbers(List.of(1, 2, 3, 4, 5, 6));
        TicketResponseDto ticket = submitTicket(List.of(1, 2, 3, 4, 5, 6));

        winningNumbersScheduler.generateWinningNumbers();
        resultCheckerScheduler.checkResults();

        // when && then
        verifyResults(ticket, 6, "WIN_MESSAGE");
        verifyResults(ticket, 6, "ALREADY_CHECKED");
    }

    @Test
    void should_handle_multiple_tickets_with_different_draw_dates() throws Exception {
        // given
        mockWinningNumbers(List.of(1, 2, 3, 4, 5, 6));
        TicketResponseDto firstTicket = submitTicket(List.of(1, 2, 3, 4, 5, 6));
        winningNumbersScheduler.generateWinningNumbers();
        resultCheckerScheduler.checkResults();

        clock.plusDays(7);

        mockWinningNumbers(List.of(4, 5, 6, 7, 8, 9));
        TicketResponseDto secondTicket = submitTicket(List.of(1, 2, 3, 4, 5, 6));

        // when && then
        verifyResults(firstTicket, 6, "WIN_MESSAGE");
        mockMvc.perform(get("/api/results/{ticketId}", secondTicket.ticketId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING_FOR_DRAW"))
                .andExpect(jsonPath("$.result").doesNotExist());
    }
}
