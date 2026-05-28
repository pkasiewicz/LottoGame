package pl.pkasiewicz.lottogame.resultannouncer.infrastructure.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.pkasiewicz.lottogame.resultannouncer.domain.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResultAnnouncerController.class)
class ResultAnnouncerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResultAnnouncerUseCase resultAnnouncerFacade;

    @Test
    void should_return_winning_result() throws Exception {
        // given
        UUID ticketId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        ResultResponse resultResponse = new ResultResponse(
                new ResultResponseId(UUID.randomUUID()),
                ticketId,
                Set.of(1, 2, 3, 4, 5, 6),
                Set.of(1, 2, 3, 4, 5, 6),
                Set.of(1, 2, 3, 4, 5, 6),
                6,
                LocalDateTime.of(2025, 11, 1, 12, 0),
                true
        );
        ResultAnnouncement resultAnnouncement = ResultAnnouncement.builder()
                .status(ResultStatus.WIN_MESSAGE)
                .result(resultResponse)
                .build();

        when(resultAnnouncerFacade.checkResult(ticketId)).thenReturn(resultAnnouncement);

        // when && then
        mockMvc.perform(get("/api/results/{ticketId}", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WIN_MESSAGE"))
                .andExpect(jsonPath("$.result.ticketId").value(ticketId.toString()))
                .andExpect(jsonPath("$.result.hitCount").value(6))
                .andExpect(jsonPath("$.result.isWinner").value(true));
    }

    @Test
    void should_return_losing_result() throws Exception {
        // given
        UUID ticketId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        ResultResponse resultResponse = new ResultResponse(
                new ResultResponseId(UUID.randomUUID()),
                ticketId,
                Set.of(7, 8, 9, 10, 11, 12),
                Set.of(1, 2, 3, 4, 5, 6),
                Set.of(1, 2, 3, 4, 5, 6),
                0,
                LocalDateTime.of(2025, 11, 1, 12, 0),
                false
        );
        ResultAnnouncement resultAnnouncement = ResultAnnouncement.builder()
                .status(ResultStatus.LOSE_MESSAGE)
                .result(resultResponse)
                .build();

        when(resultAnnouncerFacade.checkResult(ticketId)).thenReturn(resultAnnouncement);

        // when && then
        mockMvc.perform(get("/api/results/{ticketId}", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("LOSE_MESSAGE"))
                .andExpect(jsonPath("$.result.ticketId").value(ticketId.toString()))
                .andExpect(jsonPath("$.result.hitCount").value(0))
                .andExpect(jsonPath("$.result.isWinner").value(false));
    }

    @Test
    void should_return_ticket_not_found() throws Exception {
        // given
        UUID ticketId = UUID.randomUUID();
        ResultAnnouncement resultAnnouncement = ResultAnnouncement.builder()
                .status(ResultStatus.TICKET_NOT_FOUND)
                .result(null)
                .build();

        when(resultAnnouncerFacade.checkResult(ticketId)).thenReturn(resultAnnouncement);

        // when && then
        mockMvc.perform(get("/api/results/{ticketId}", ticketId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("TICKET_NOT_FOUND"))
                .andExpect(jsonPath("$.result").doesNotExist());
    }

    @Test
    void should_return_waiting_for_draw() throws Exception {
        // given
        UUID ticketId = UUID.randomUUID();
        ResultAnnouncement resultAnnouncement = ResultAnnouncement.builder()
                .status(ResultStatus.WAITING_FOR_DRAW)
                .result(null)
                .build();

        when(resultAnnouncerFacade.checkResult(ticketId)).thenReturn(resultAnnouncement);

        // when && then
        mockMvc.perform(get("/api/results/{ticketId}", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING_FOR_DRAW"))
                .andExpect(jsonPath("$.result").doesNotExist());
    }

    @Test
    void should_return_already_checked() throws Exception {
        // given
        UUID ticketId = UUID.randomUUID();
        ResultResponse resultResponse = new ResultResponse(
                new ResultResponseId(UUID.randomUUID()),
                ticketId,
                Set.of(1, 2, 3, 4, 5, 6),
                Set.of(1, 2, 3, 4, 5, 6),
                Set.of(1, 2, 3, 4, 5, 6),
                6,
                LocalDateTime.of(2025, 11, 1, 12, 0),
                true
        );
        ResultAnnouncement resultAnnouncement = ResultAnnouncement.builder()
                .status(ResultStatus.ALREADY_CHECKED)
                .result(resultResponse)
                .build();

        when(resultAnnouncerFacade.checkResult(ticketId)).thenReturn(resultAnnouncement);

        // when && then
        mockMvc.perform(get("/api/results/{ticketId}", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ALREADY_CHECKED"));
    }
}