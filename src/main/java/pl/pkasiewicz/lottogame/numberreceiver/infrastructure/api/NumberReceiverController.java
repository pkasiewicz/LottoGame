package pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.pkasiewicz.lottogame.numberreceiver.domain.Ticket;
import pl.pkasiewicz.lottogame.numberreceiver.domain.port.NumberReceiverUseCase;
import pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api.dto.InputNumbersRequestDto;
import pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api.dto.TicketResponseDto;

/**
 * REST controller for handling number submission for the lottery game.
 * Delegates business logic to {@link NumberReceiverUseCase}.
 */
@RestController
@AllArgsConstructor
public class NumberReceiverController implements NumberReceiverApi {

    private final NumberReceiverUseCase numberReceiverFacade;

    @Override
    public ResponseEntity<TicketResponseDto> submitNumbers(@RequestBody @Valid InputNumbersRequestDto request) {
        Ticket ticket = numberReceiverFacade.inputNumbers(request.numbers());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TicketResponseDto.from(ticket));
    }
}
