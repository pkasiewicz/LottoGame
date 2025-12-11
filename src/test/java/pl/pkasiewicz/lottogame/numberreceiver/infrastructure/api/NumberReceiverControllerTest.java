package pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.pkasiewicz.lottogame.numberreceiver.domain.NumberReceiverUseCase;
import pl.pkasiewicz.lottogame.numberreceiver.domain.Ticket;
import pl.pkasiewicz.lottogame.numberreceiver.domain.TicketId;
import pl.pkasiewicz.lottogame.numberreceiver.domain.exception.InvalidTicketNumbersException;
import pl.pkasiewicz.lottogame.numberreceiver.domain.exception.InvalidTicketSizeException;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NumberReceiverController.class)
class NumberReceiverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NumberReceiverUseCase numberReceiverFacade;

    @Test
    void should_return_created_ticket_when_valid_numbers() throws Exception {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);
        Ticket ticket = new Ticket(
                new TicketId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                numbers,
                LocalDateTime.of(2025, 11, 1, 12, 0)
        );
        when(numberReceiverFacade.inputNumbers(numbers)).thenReturn(ticket);

        // when && then
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                
                                            {"numbers": [1, 2, 3, 4, 5, 6]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketId").value("00000000-0000-0000-0000-000000000001"))
                .andExpect(jsonPath("$.numbers").isArray())
                .andExpect(jsonPath("$.numbers", hasSize(6)))
                .andExpect(jsonPath("$.drawDate").exists());
    }

    @Test
    void should_return_bad_request_when_invalid_ticket_size() throws Exception {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5);
        when(numberReceiverFacade.inputNumbers(numbers)).thenThrow(new InvalidTicketSizeException("Expected 6 numbers, got 5"));

        // when && then
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"numbers": [1, 2, 3, 4, 5]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_TICKET_SIZE"))
                .andExpect(jsonPath("$.message").value("Expected 6 numbers, got 5"));
    }

    @Test
    void should_return_bad_request_when_numbers_out_of_range() throws Exception {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 100);
        when(numberReceiverFacade.inputNumbers(numbers)).thenThrow(new InvalidTicketNumbersException("Numbers must be between 1 and 99"));

        // when & then
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"numbers": [1, 2, 3, 4, 5, 100]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_NUMBERS"))
                .andExpect(jsonPath("$.message").value("Numbers must be between 1 and 99"));
    }

    @Test
    void should_return_bad_request_when_numbers_null() throws Exception {
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
    void should_return_bad_request_when_numbers_empty() throws Exception {
        // given
        String requestBody = """
                {
                    "numbers": []
                }
                """;
        when(numberReceiverFacade.inputNumbers(Set.of())).thenThrow(new InvalidTicketSizeException("Expected 6 numbers, got 0"));

        // when && then
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_TICKET_SIZE"))
                .andExpect(jsonPath("$.message").value("Expected 6 numbers, got 0"));
    }
}