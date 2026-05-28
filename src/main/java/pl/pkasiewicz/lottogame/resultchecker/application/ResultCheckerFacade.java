package pl.pkasiewicz.lottogame.resultchecker.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pkasiewicz.lottogame.domain.DrawDateGenerable;
import pl.pkasiewicz.lottogame.domain.IdGenerable;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbers;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbersGeneratorUseCase;
import pl.pkasiewicz.lottogame.numberreceiver.domain.NumberReceiverUseCase;
import pl.pkasiewicz.lottogame.numberreceiver.domain.Ticket;
import pl.pkasiewicz.lottogame.resultchecker.domain.ResultCheckerUseCase;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResult;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResultId;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResultRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ResultCheckerFacade implements ResultCheckerUseCase {

    private final TicketResultRepository repository;
    private final WinningNumbersGeneratorUseCase winningNumbersGeneratorFacade;
    private final NumberReceiverUseCase numberReceiverFacade;
    private final DrawDateGenerable drawDateGenerator;
    private final IdGenerable idGenerator;

    @Transactional
    @Override
    public List<TicketResult> generateResults() {
        LocalDateTime nextDrawDate = drawDateGenerator.getNextDrawDate();

        if (!winningNumbersGeneratorFacade.areWinningNumbersGeneratedByDate()) {
            return List.of();
        }

        WinningNumbers winningNumbers = winningNumbersGeneratorFacade.retrieveWinningNumbersByDate(nextDrawDate);
        Set<Integer> wonNumbers = winningNumbers.getWinningNumbers();

        List<Ticket> tickets = numberReceiverFacade.retrieveAllTicketsByNextDrawDate(nextDrawDate);

        return tickets.stream()
                .filter(ticket -> repository.findByTicketId(ticket.getId().value()).isEmpty())
                .map(ticket -> TicketResult.fromTicket(ticket,
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
