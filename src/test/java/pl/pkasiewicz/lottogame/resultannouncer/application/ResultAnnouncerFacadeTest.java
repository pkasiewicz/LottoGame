package pl.pkasiewicz.lottogame.resultannouncer.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pkasiewicz.lottogame.infrastructure.IdGenerator;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbers;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbersGeneratorUseCase;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbersId;
import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultAnnouncement;
import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultResponse;
import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultStatus;
import pl.pkasiewicz.lottogame.resultannouncer.testhelpers.InMemoryResultResponseRepository;
import pl.pkasiewicz.lottogame.resultchecker.domain.ResultCheckerUseCase;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResult;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResultId;
import pl.pkasiewicz.lottogame.resultchecker.domain.exception.TicketResultNotFoundException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultAnnouncerFacadeTest {

    public static final LocalDateTime DRAW_DATE = LocalDateTime.of(2025, 11, 1, 12, 0);
    public static final Set<Integer> WINNING_NUMBERS = Set.of(1, 2, 3, 4, 5, 6);

    private ResultAnnouncerFacade resultAnnouncerFacade;
    private ResultCheckerUseCase resultCheckerFacade;
    private WinningNumbersGeneratorUseCase numberGenerator;


    @BeforeEach
    void setUp() {
        InMemoryResultResponseRepository repository = new InMemoryResultResponseRepository();
        resultCheckerFacade = mock(ResultCheckerUseCase.class);
        numberGenerator = mock(WinningNumbersGeneratorUseCase.class);

        resultAnnouncerFacade = new ResultAnnouncerFacade(
                repository,
                resultCheckerFacade,
                numberGenerator,
                new IdGenerator(),
                Clock.systemUTC()
        );
    }

    @Test
    public void should_return_win_status_for_winning_ticket() {
        // given
        TicketResult ticketResult = createWinningTicketResult();
        mockTicketResultAndWinningNumbers(ticketResult);

        // when
        ResultAnnouncement result = resultAnnouncerFacade.checkResult(ticketResult.getTicketId());

        // then
        assertThat(result.status()).isEqualTo(ResultStatus.WIN_MESSAGE);
        assertResultEquals(result.result(), ticketResult);
    }

    @Test
    public void should_return_lose_status_for_losing_ticket() {
        // given
        TicketResult ticketResult = createLosingTicketResult();
        mockTicketResultAndWinningNumbers(ticketResult);

        // when
        ResultAnnouncement result = resultAnnouncerFacade.checkResult(ticketResult.getTicketId());

        // then
        assertThat(result.status()).isEqualTo(ResultStatus.LOSE_MESSAGE);
        assertResultEquals(result.result(), ticketResult);
    }

    @Test
    public void should_return_ticket_not_found_status_for_non_existing_ticket() {
        // given
        when(resultCheckerFacade.getResultForTicket(any(UUID.class))).thenThrow(TicketResultNotFoundException.class);

        // when
        ResultAnnouncement result = resultAnnouncerFacade.checkResult(UUID.randomUUID());

        // then
        assertThat(result.status()).isEqualTo(ResultStatus.TICKET_NOT_FOUND);
    }

    @Test
    public void should_return_already_checked_status_for_cached_ticket() {
        // given
        TicketResult ticketResult = createWinningTicketResult();
        mockTicketResultAndWinningNumbers(ticketResult);
        resultAnnouncerFacade.checkResult(ticketResult.getTicketId());

        // when
        ResultAnnouncement result = resultAnnouncerFacade.checkResult(ticketResult.getTicketId());

        // then
        assertThat(result.status()).isEqualTo(ResultStatus.ALREADY_CHECKED);
    }

    @Test
    public void should_return_waiting_for_draw_status_if_draw_not_yet_occurred() {
        // given
        when(resultCheckerFacade.getDrawDateForTicket(any(UUID.class))).thenReturn(Optional.empty());

        // when
        ResultAnnouncement result = resultAnnouncerFacade.checkResult(UUID.randomUUID());

        // then
        assertThat(result.status()).isEqualTo(ResultStatus.WAITING_FOR_DRAW);
    }

    private TicketResult createWinningTicketResult() {
        return new TicketResult(
                new TicketResultId(UUID.randomUUID()),
                UUID.randomUUID(),
                Set.of(1, 2, 3, 4, 5, 6),
                Set.of(1, 2, 3, 4, 5, 6),
                6,
                DRAW_DATE,
                true
        );
    }

    private TicketResult createLosingTicketResult() {
        return new TicketResult(
                new TicketResultId(UUID.randomUUID()),
                UUID.randomUUID(),
                Set.of(7, 8, 9, 10, 11, 12),
                Set.of(),
                0,
                DRAW_DATE,
                false
        );
    }

    private void mockTicketResultAndWinningNumbers(TicketResult ticketResult) {
        when(resultCheckerFacade.getResultForTicket(ticketResult.getTicketId()))
                .thenReturn(ticketResult);
        when(resultCheckerFacade.getDrawDateForTicket(ticketResult.getTicketId()))
                .thenReturn(Optional.of(DRAW_DATE));
        when(numberGenerator.retrieveWinningNumbersByDate(any(LocalDateTime.class)))
                .thenReturn(new WinningNumbers(
                        new WinningNumbersId(UUID.randomUUID()),
                        WINNING_NUMBERS,
                        DRAW_DATE
                ));
    }

    private void assertResultEquals(ResultResponse actual, TicketResult expected) {
        assertAll(
                () -> assertThat(actual.getTicketId()).isEqualTo(expected.getTicketId()),
                () -> assertThat(actual.getUserNumbers()).isEqualTo(expected.getUserNumbers()),
                () -> assertThat(actual.getWonNumbers()).isEqualTo(WINNING_NUMBERS),
                () -> assertThat(actual.getHitNumbers()).isEqualTo(expected.getHitNumbers()),
                () -> assertThat(actual.getHitCount()).isEqualTo(expected.getHitCount()),
                () -> assertThat(actual.getDrawDate()).isEqualTo(expected.getDrawDate()),
                () -> assertThat(actual.isWinner()).isEqualTo(expected.isWinner())
        );
    }
}