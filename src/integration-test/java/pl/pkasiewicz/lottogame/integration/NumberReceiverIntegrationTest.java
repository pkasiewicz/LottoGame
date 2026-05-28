package pl.pkasiewicz.lottogame.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import pl.pkasiewicz.lottogame.numberreceiver.domain.TicketRepository;
import pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api.dto.TicketResponseDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NumberReceiverIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    public void setup() {
        ticketRepository.deleteAll();
    }

    @Test
    void should_successfully_submit_valid_ticket() throws Exception {
        // given
        String requestBody = """
                {
                    "numbers": [1, 2, 3, 4, 5, 6]
                }
                """;

        // when && then
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketId").exists())
                .andExpect(jsonPath("$.ticketId").isString())
                .andExpect(jsonPath("$.numbers", hasSize(6)))
                .andExpect(jsonPath("$.numbers", containsInAnyOrder(1, 2, 3, 4, 5, 6)))
                .andExpect(jsonPath("$.drawDate").exists())
                .andReturn();

        assertThat(ticketRepository.findAll()).hasSize(1);
    }

    @Test
    void should_return_bad_request_when_ticket_has_invalid_size() throws Exception {
        // given - less than 6 numbers
        String requestBody = """
                {
                    "numbers": [1, 2, 3, 4, 5]
                }
                """;

        // when && then
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_TICKET_SIZE"))
                .andExpect(jsonPath("$.message").value("Expected 6 numbers, got 5"));
    }

    @Test
    void should_return_bad_request_when_ticket_has_too_many_numbers() throws Exception {
        // given - more than 6 numbers
        String requestBody = """
                {
                    "numbers": [1, 2, 3, 4, 5, 6, 7]
                }
                """;

        // when && then
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_TICKET_SIZE"))
                .andExpect(jsonPath("$.message").value("Expected 6 numbers, got 7"));
    }

    @Test
    void should_return_bad_request_when_numbers_are_out_of_range() throws Exception {
        // given - number 0 is out of range
        String requestBody = """
                {
                    "numbers": [0, 2, 3, 4, 5, 6]
                }
                """;

        // when && then
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_NUMBERS"))
                .andExpect(jsonPath("$.message").value("Numbers must be between 1 and 99"));
    }

    @Test
    void should_return_bad_request_when_numbers_exceed_maximum() throws Exception {
        // given - number 100 is out of range
        String requestBody = """
                {
                    "numbers": [1, 2, 3, 4, 5, 100]
                }
                """;

        // when && then
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_NUMBERS"))
                .andExpect(jsonPath("$.message").value("Numbers must be between 1 and 99"));
    }

    @Test
    void should_create_multiple_tickets_with_unique_ids() throws Exception {
        // given
        String requestBody1 = """
                {
                    "numbers": [1, 2, 3, 4, 5, 6]
                }
                """;
        String requestBody2 = """
                {
                    "numbers": [7, 8, 9, 10, 11, 12]
                }
                """;

        // when
        MvcResult result1 = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody1))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult result2 = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2))
                .andExpect(status().isCreated())
                .andReturn();

        // then
        String response1 = result1.getResponse().getContentAsString();
        String response2 = result2.getResponse().getContentAsString();

        TicketResponseDto ticket1 = objectMapper.readValue(response1, TicketResponseDto.class);
        TicketResponseDto ticket2 = objectMapper.readValue(response2, TicketResponseDto.class);

        assertThat(ticket1.ticketId()).isNotEqualTo(ticket2.ticketId());
        assertThat(ticketRepository.findAll()).hasSize(2);
    }

    @Test
    void should_assign_same_draw_date_for_tickets_submitted_in_same_draw_period() throws Exception {
        // given
        String requestBody1 = """
                {
                    "numbers": [1, 2, 3, 4, 5, 6]
                }
                """;
        String requestBody2 = """
                {
                    "numbers": [7, 8, 9, 10, 11, 12]
                }
                """;

        // when
        MvcResult result1 = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody1))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult result2 = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2))
                .andExpect(status().isCreated())
                .andReturn();

        // then
        String drawDate1 = objectMapper.readTree(result1.getResponse().getContentAsString()).get("drawDate").asText();
        String drawDate2 = objectMapper.readTree(result2.getResponse().getContentAsString()).get("drawDate").asText();

        assertThat(drawDate1).isEqualTo(drawDate2);
    }

    @Test
    void should_return_bad_request_when_numbers_is_null() throws Exception {
        // given
        String requestBody = """
                {
                    "numbers": null
                }
                """;

        // when && then
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Numbers cannot be null"));
    }

    @Test
    void should_return_bad_request_when_numbers_is_empty() throws Exception {
        // given
        String requestBody = """
                {
                    "numbers": []
                }
                """;

        // when && then
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_TICKET_SIZE"))
                .andExpect(jsonPath("$.message").value("Expected 6 numbers, got 0"));
    }

    @Test
    void should_return_bad_request_when_numbers_contain_duplicates() throws Exception {
        // given
        String requestBody = """
                {
                    "numbers": [1, 1, 2, 3, 4, 5]
                }
                """;

        // when && then
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_TICKET_SIZE"))
                .andExpect(jsonPath("$.message").value("Expected 6 numbers, got 5"));
    }
}