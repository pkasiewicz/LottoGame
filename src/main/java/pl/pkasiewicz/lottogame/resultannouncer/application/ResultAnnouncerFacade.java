package pl.pkasiewicz.lottogame.resultannouncer.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pkasiewicz.lottogame.domain.port.IdGenerable;
import pl.pkasiewicz.lottogame.domain.port.WinningNumbersProvider;
import pl.pkasiewicz.lottogame.resultannouncer.domain.*;
import pl.pkasiewicz.lottogame.resultannouncer.domain.port.ResultAnnouncerUseCase;
import pl.pkasiewicz.lottogame.resultannouncer.domain.port.ResultResponseRepository;
import pl.pkasiewicz.lottogame.resultannouncer.domain.port.TicketExistenceChecker;
import pl.pkasiewicz.lottogame.resultannouncer.domain.port.TicketResultProvider;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Facade for announcing lottery results.
 * Retrieves ticket results and winning numbers, builds result announcements, and caches them for future requests.
 */
@Service
@AllArgsConstructor
public class ResultAnnouncerFacade implements ResultAnnouncerUseCase {

    private final ResultResponseRepository repository;
    private final TicketExistenceChecker ticketExistenceChecker;
    private final TicketResultProvider ticketResultProvider;
    private final WinningNumbersProvider winningNumbersProvider;
    private final IdGenerable idGenerator;

    @Override
    public ResultAnnouncement checkResult(UUID ticketId) {
        Optional<ResultResponse> cached = repository.findByTicketId(ticketId);
        if (cached.isPresent()) {
            return ResultAnnouncement.builder()
                    .status(ResultStatus.ALREADY_CHECKED)
                    .result(cached.get())
                    .build();
        }

        if (!ticketExistenceChecker.ticketExistsById(ticketId)) {
            return ResultAnnouncement.builder()
                    .status(ResultStatus.TICKET_NOT_FOUND)
                    .result(null)
                    .build();
        }

        Optional<TicketResultData> ticketResult = ticketResultProvider.getResultForTicket(ticketId);
        if (ticketResult.isEmpty()) {
            return ResultAnnouncement.builder()
                    .status(ResultStatus.WAITING_FOR_DRAW)
                    .result(null)
                    .build();
        }

        Set<Integer> winningNumbers = winningNumbersProvider.getWinningNumbersByDate(ticketResult.get().drawDate());
        ResultResponse saved = buildAndSaveResult(ticketResult.get(), winningNumbers);

        return ResultAnnouncement.builder()
                .status(saved.isWinner() ? ResultStatus.WIN_MESSAGE : ResultStatus.LOSE_MESSAGE)
                .result(saved)
                .build();
    }

    private ResultResponse buildAndSaveResult(TicketResultData ticketResult, Set<Integer> winningNumbers) {
        return repository.save(
                new ResultResponse(
                        new ResultResponseId(idGenerator.generateId()),
                        ticketResult.ticketId(),
                        ticketResult.userNumbers(),
                        winningNumbers,
                        ticketResult.hitNumbers(),
                        ticketResult.hitCount(),
                        ticketResult.drawDate(),
                        ticketResult.isWinner()
                )
        );
    }
}
