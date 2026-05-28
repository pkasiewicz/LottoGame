package pl.pkasiewicz.lottogame.resultannouncer.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pkasiewicz.lottogame.domain.IdGenerable;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbers;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbersGeneratorUseCase;
import pl.pkasiewicz.lottogame.numberreceiver.domain.NumberReceiverUseCase;
import pl.pkasiewicz.lottogame.resultannouncer.domain.*;
import pl.pkasiewicz.lottogame.resultchecker.domain.ResultCheckerUseCase;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResult;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ResultAnnouncerFacade implements ResultAnnouncerUseCase {

    private final ResultResponseRepository repository;
    private final NumberReceiverUseCase numberReceiverFacade;
    private final ResultCheckerUseCase resultCheckerFacade;
    private final WinningNumbersGeneratorUseCase numberGenerator;
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

        if (!numberReceiverFacade.ticketExists(ticketId)) {
            return ResultAnnouncement.builder()
                    .status(ResultStatus.TICKET_NOT_FOUND)
                    .result(null)
                    .build();
        }

        Optional<TicketResult> ticketResult = resultCheckerFacade.getResultForTicket(ticketId);
        if (ticketResult.isEmpty()) {
            return ResultAnnouncement.builder()
                    .status(ResultStatus.WAITING_FOR_DRAW)
                    .result(null)
                    .build();
        }

        WinningNumbers winningNumbers = numberGenerator.retrieveWinningNumbersByDate(ticketResult.get().getDrawDate());
        ResultResponse saved = buildAndSaveResult(ticketResult.get(), winningNumbers.getWinningNumbers());

        return ResultAnnouncement.builder()
                .status(saved.isWinner() ? ResultStatus.WIN_MESSAGE : ResultStatus.LOSE_MESSAGE)
                .result(saved)
                .build();
    }

    private ResultResponse buildAndSaveResult(TicketResult ticketResult, Set<Integer> winningNumbers) {
        return repository.save(new ResultResponse(
                new ResultResponseId(idGenerator.generateId()),
                ticketResult.getTicketId(),
                ticketResult.getUserNumbers(),
                winningNumbers,
                ticketResult.getHitNumbers(),
                ticketResult.getHitCount(),
                ticketResult.getDrawDate(),
                ticketResult.isWinner()
        ));
    }
}
