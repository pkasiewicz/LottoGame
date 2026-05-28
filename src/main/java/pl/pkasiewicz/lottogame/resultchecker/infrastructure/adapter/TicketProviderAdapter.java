package pl.pkasiewicz.lottogame.resultchecker.infrastructure.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pkasiewicz.lottogame.numberreceiver.domain.port.NumberReceiverUseCase;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketData;
import pl.pkasiewicz.lottogame.resultchecker.domain.port.TicketProvider;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
class TicketProviderAdapter implements TicketProvider {

    private final NumberReceiverUseCase numberReceiverFacade;

    @Override
    public List<TicketData> getTicketsByDrawDate(LocalDateTime drawDate) {
        return numberReceiverFacade.retrieveAllTicketsByNextDrawDate(drawDate).stream()
                .map(ticket -> new TicketData(
                        ticket.getId().value(),
                        ticket.getNumbers(),
                        ticket.getDrawDate()
                ))
                .toList();
    }
}
