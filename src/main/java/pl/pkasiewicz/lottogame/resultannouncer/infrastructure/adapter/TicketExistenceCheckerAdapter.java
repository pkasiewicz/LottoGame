package pl.pkasiewicz.lottogame.resultannouncer.infrastructure.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pkasiewicz.lottogame.numberreceiver.domain.port.NumberReceiverUseCase;
import pl.pkasiewicz.lottogame.resultannouncer.domain.port.TicketExistenceChecker;

import java.util.UUID;

@Component
@AllArgsConstructor
class TicketExistenceCheckerAdapter implements TicketExistenceChecker {

    private final NumberReceiverUseCase numberReceiverFacade;

    @Override
    public boolean ticketExistsById(UUID ticketId) {
        return numberReceiverFacade.ticketExists(ticketId);
    }
}
