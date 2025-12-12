package pl.pkasiewicz.lottogame.resultchecker.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pkasiewicz.lottogame.domain.DrawDateGenerable;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbers;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbersGeneratorUseCase;
import pl.pkasiewicz.lottogame.numberreceiver.domain.NumberReceiverUseCase;
import pl.pkasiewicz.lottogame.numberreceiver.domain.Ticket;
import pl.pkasiewicz.lottogame.resultchecker.domain.ResultCheckerUseCase;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResult;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResultRepository;
import pl.pkasiewicz.lottogame.resultchecker.domain.exception.TicketResultNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ResultCheckerFacade implements ResultCheckerUseCase {

    private final TicketResultRepository repository;
    private final WinningNumbersGeneratorUseCase winningNumbersGeneratorFacade;
    private final NumberReceiverUseCase numberReceiverFacade;
    private final DrawDateGenerable drawDateGenerator;

    @Override
    public List<TicketResult> generateResults() {
        LocalDateTime nextDrawDate = drawDateGenerator.getNextDrawDate();

        WinningNumbers winningNumbers = winningNumbersGeneratorFacade.retrieveWinningNumbersByDate(nextDrawDate);
        Set<Integer> wonNumbers = winningNumbers.getWinningNumbers();

        List<Ticket> tickets = numberReceiverFacade.retrieveAllTicketsByNextDrawDate(nextDrawDate);

        return tickets.stream()
                .map(ticket -> TicketResult.fromTicket(ticket, wonNumbers))
                .map(repository::save)
                .toList();
    }

    @Override
    public TicketResult getResultForTicket(UUID ticketId) {
        return repository.findByTicketId(ticketId)
                .orElseThrow(() -> new TicketResultNotFoundException("Ticket result with TicketId: " + ticketId + " not found"));
    }
}
