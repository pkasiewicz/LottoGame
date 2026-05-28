package pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api.dto.InputNumbersRequestDto;
import pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api.dto.TicketResponseDto;

@Tag(name = "Number Receiver", description = "Endpoint for submitting lottery ticket numbers")
@RequestMapping("/api/tickets")
public interface NumberReceiverApi {

    @PostMapping
    @Operation(
            summary = "Submit lottery numbers",
            description = "Submit 6 unique numbers between 1 and 99 to participate in the lottery. " +
                    "Returns a ticket with a unique ID, provided numbers and the draw date (next Saturday at 12:00)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ticket created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input - numbers must be between 1-99, exactly 6 unique numbers required")
    })
    ResponseEntity<TicketResponseDto> submitNumbers(@RequestBody @Valid InputNumbersRequestDto request);
}