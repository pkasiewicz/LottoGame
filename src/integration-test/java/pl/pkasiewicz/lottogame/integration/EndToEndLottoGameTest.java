package pl.pkasiewicz.lottogame.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import pl.pkasiewicz.lottogame.numbergenerator.infrastructure.scheduler.WinningNumbersScheduler;
import pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api.dto.TicketResponseDto;
import pl.pkasiewicz.lottogame.resultannouncer.infrastructure.api.dto.ResultAnnouncementDto;
import pl.pkasiewicz.lottogame.resultchecker.infrastructure.scheduler.ResultCheckerScheduler;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EndToEndLottoGameTest extends BaseIntegrationTest {

    @Autowired
    private WinningNumbersScheduler winningNumbersScheduler;

    @Autowired
    private ResultCheckerScheduler resultCheckerScheduler;

    @Test
    void complete_lotto_game_flow_for_winning_and_losing_ticket() throws Exception {
        // STEP 1: external service returns 6 random numbers (1, 2, 3, 4, 5, 6)
        mockWinningNumbers(List.of(1, 2, 3, 4, 5, 6));


        // STEP 2: users made POST /inputNumbers with 6 numbers (1, 2, 3, 4, 5, 6) and (7, 8, 9, 10, 11, 12) at 01-12-2025 10:00
        TicketResponseDto winningTicket = submitTicket(List.of(1, 2, 3, 4, 5, 6));
        TicketResponseDto losingTicket = submitTicket(List.of(7, 8, 9, 10, 11, 12));


        // STEP 3: system generated winning numbers for draw date: 06.12.2025 12:00
        winningNumbersScheduler.generateWinningNumbers();


        // STEP 4: system generated results for winning and losing ticket
        resultCheckerScheduler.checkResults();


        // STEP 5: Advance time past draw date
        clock.plusDaysAndMinutes(5, 195);


        // STEP 6: users made GET /results/sampleTicketId and system returned 200 (OK) with correct results
        verifyResults(winningTicket, 6, "WIN_MESSAGE");
        verifyResults(losingTicket, 0, "LOSE_MESSAGE");
    }

    private void mockWinningNumbers(List<Integer> numbers) {
        String jsonArray = numbers.toString();

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/v1.0/random"))
                .withQueryParam("count", WireMock.equalTo("6"))
                .withQueryParam("min", WireMock.equalTo("1"))
                .withQueryParam("max", WireMock.equalTo("99"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonArray))
        );
    }

    private TicketResponseDto submitTicket(List<Integer> numbers) throws Exception {
        String jsonArray = numbers.toString();
        String requestBody = String.format("{\"numbers\": %s}", jsonArray);

        String contentAsString = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketId").exists())
                .andExpect(jsonPath("$.numbers", hasSize(6)))
                .andExpect(jsonPath("$.drawDate").exists())
                .andReturn()
                .getResponse().getContentAsString();

        return objectMapper.readValue(contentAsString, TicketResponseDto.class);
    }

    private void verifyResults(TicketResponseDto ticket, int expectedHitCount, String expectedStatus) throws Exception {
        String resultJson = mockMvc.perform(get("/api/results/{ticketId}", ticket.ticketId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ResultAnnouncementDto result = objectMapper.readValue(resultJson, ResultAnnouncementDto.class);

        assertAll(
                () -> assertThat(result.status()).isEqualTo(expectedStatus),
                () -> assertThat(result.result().ticketId()).isEqualTo(ticket.ticketId()),
                () -> assertThat(result.result().userNumbers()).isEqualTo(ticket.numbers()),
                () -> assertThat(result.result().drawDate()).isEqualTo(ticket.drawDate()),
                () -> assertThat(result.result().hitCount()).isEqualTo(expectedHitCount)
        );
    }
}
