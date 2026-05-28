package pl.pkasiewicz.lottogame.resultannouncer.infrastructure.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pkasiewicz.lottogame.resultannouncer.domain.TicketResultData;
import pl.pkasiewicz.lottogame.resultannouncer.domain.port.TicketResultProvider;
import pl.pkasiewicz.lottogame.resultchecker.domain.port.ResultCheckerUseCase;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
class TicketResultProviderAdapter implements TicketResultProvider {

    private final ResultCheckerUseCase resultCheckerFacade;

    @Override
    public Optional<TicketResultData> getResultForTicket(UUID ticketId) {
        return resultCheckerFacade.getResultForTicket(ticketId).map(result -> new TicketResultData(
                result.getId().value(),
                result.getTicketId(),
                result.getUserNumbers(),
                result.getHitNumbers(),
                result.getHitCount(),
                result.getDrawDate(),
                result.isWinner()
        ));
    }
}
