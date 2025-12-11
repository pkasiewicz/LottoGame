package pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pkasiewicz.lottogame.numberreceiver.domain.NumberReceiverUseCase;
import pl.pkasiewicz.lottogame.numberreceiver.domain.Ticket;
import pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api.dto.InputNumbersRequestDto;
import pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api.dto.TicketResponseDto;

@RestController
@RequestMapping("/api/tickets")
@AllArgsConstructor
public class NumberReceiverController {

    private final NumberReceiverUseCase numberReceiverFacade;

    @PostMapping
    public ResponseEntity<TicketResponseDto> submitNumbers(@RequestBody @Valid InputNumbersRequestDto request) {
        Ticket ticket = numberReceiverFacade.inputNumbers(request.numbers());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TicketResponseDto.from(ticket));
    }
}
