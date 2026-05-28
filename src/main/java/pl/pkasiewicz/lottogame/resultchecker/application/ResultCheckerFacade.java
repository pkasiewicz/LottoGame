package pl.pkasiewicz.lottogame.resultchecker.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pkasiewicz.lottogame.domain.port.DrawDateGenerable;
import pl.pkasiewicz.lottogame.domain.port.IdGenerable;
import pl.pkasiewicz.lottogame.domain.port.WinningNumbersProvider;
import pl.pkasiewicz.lottogame.resultchecker.domain.*;
import pl.pkasiewicz.lottogame.resultchecker.domain.port.ResultCheckerUseCase;
import pl.pkasiewicz.lottogame.resultchecker.domain.port.TicketProvider;
import pl.pkasiewicz.lottogame.resultchecker.domain.port.TicketResultRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ResultCheckerFacade implements ResultCheckerUseCase {

    private final TicketResultRepository repository;
    private final WinningNumbersProvider winningNumbersProvider;
    private final TicketProvider ticketProvider;
    private final DrawDateGenerable drawDateGenerator;
    private final IdGenerable idGenerator;

    @Transactional
    @Override
    public List<TicketResult> generateResults() {
        LocalDateTime nextDrawDate = drawDateGenerator.getNextDrawDate();

        if (!winningNumbersProvider.areWinningNumbersGeneratedByDate()) {
            return List.of();
        }

        Set<Integer> wonNumbers = winningNumbersProvider.getWinningNumbersByDate(nextDrawDate);

        List<TicketData> tickets = ticketProvider.getTicketsByDrawDate(nextDrawDate);

        return tickets.stream()
                .filter(ticket -> repository.findByTicketId(ticket.ticketId()).isEmpty())
                .map(ticket -> TicketResult.fromTicketData(ticket,
                        wonNumbers,
                        new TicketResultId(idGenerator.generateId())))
                .map(repository::save)
                .toList();
    }

    @Override
    public Optional<TicketResult> getResultForTicket(UUID ticketId) {
        return repository.findByTicketId(ticketId);
    }
}
