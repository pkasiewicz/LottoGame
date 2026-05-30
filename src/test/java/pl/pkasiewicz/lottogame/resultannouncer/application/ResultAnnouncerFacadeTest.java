package pl.pkasiewicz.lottogame.resultannouncer.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pkasiewicz.lottogame.domain.port.IdGenerable;
import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultAnnouncement;
import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultResponse;
import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultStatus;
import pl.pkasiewicz.lottogame.resultannouncer.domain.TicketResultData;
import pl.pkasiewicz.lottogame.resultannouncer.domain.port.TicketExistenceChecker;
import pl.pkasiewicz.lottogame.resultannouncer.domain.port.TicketResultProvider;
import pl.pkasiewicz.lottogame.resultannouncer.testhelpers.InMemoryResultResponseRepository;

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
    private TicketExistenceChecker ticketExistenceChecker;
    private TicketResultProvider ticketResultProvider;
    private IdGenerable idGenerator;

    @BeforeEach
    void setUp() {
        InMemoryResultResponseRepository repository = new InMemoryResultResponseRepository();
        ticketExistenceChecker = mock(TicketExistenceChecker.class);
        ticketResultProvider = mock(TicketResultProvider.class);
        idGenerator = mock(IdGenerable.class);

        resultAnnouncerFacade = new ResultAnnouncerFacade(
                repository,
                ticketExistenceChecker,
                ticketResultProvider,
                idGenerator
        );
    }

    @Test
    public void should_return_win_status_for_winning_ticket() {
        // given

        TicketResultData ticketResult = createWinningTicketResultData();
        mockTicketResultAndWinningNumbers(ticketResult);

        // when
        ResultAnnouncement result = resultAnnouncerFacade.checkResult(ticketResult.ticketId());

        // then
        assertThat(result.status()).isEqualTo(ResultStatus.WIN_MESSAGE);
        assertResultEquals(result.result(), ticketResult);
    }

    @Test
    public void should_return_lose_status_for_losing_ticket() {
        // given
        TicketResultData ticketResult = createLosingTicketResultData();
        mockTicketResultAndWinningNumbers(ticketResult);

        // when
        ResultAnnouncement result = resultAnnouncerFacade.checkResult(ticketResult.ticketId());

        // then
        assertThat(result.status()).isEqualTo(ResultStatus.LOSE_MESSAGE);
        assertResultEquals(result.result(), ticketResult);
    }

    @Test
    public void should_return_ticket_not_found_status_for_non_existing_ticket() {
        // given
        when(ticketExistenceChecker.ticketExistsById(any(UUID.class))).thenReturn(false);

        // when
        ResultAnnouncement result = resultAnnouncerFacade.checkResult(UUID.randomUUID());

        // then
        assertThat(result.status()).isEqualTo(ResultStatus.TICKET_NOT_FOUND);
    }

    @Test
    public void should_return_already_checked_status_for_cached_ticket() {
        // given
        TicketResultData ticketResult = createWinningTicketResultData();
        mockTicketResultAndWinningNumbers(ticketResult);
        resultAnnouncerFacade.checkResult(ticketResult.ticketId());

        // when
        ResultAnnouncement result = resultAnnouncerFacade.checkResult(ticketResult.ticketId());

        // then
        assertThat(result.status()).isEqualTo(ResultStatus.ALREADY_CHECKED);
    }

    @Test
    public void should_return_waiting_for_draw_status_if_draw_not_yet_occurred() {
        // given
        when(ticketExistenceChecker.ticketExistsById(any(UUID.class))).thenReturn(true);
        when(ticketResultProvider.getResultForTicket(any(UUID.class))).thenReturn(Optional.empty());

        // when
        ResultAnnouncement result = resultAnnouncerFacade.checkResult(UUID.randomUUID());

        // then
        assertThat(result.status()).isEqualTo(ResultStatus.WAITING_FOR_DRAW);
    }

    private TicketResultData createWinningTicketResultData() {
        return new TicketResultData(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(1, 2, 3, 4, 5, 6),
                Set.of(1, 2, 3, 4, 5, 6),
                Set.of(1, 2, 3, 4, 5, 6),
                6,
                DRAW_DATE,
                true
        );
    }

    private TicketResultData createLosingTicketResultData() {
        return new TicketResultData(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(7, 8, 9, 10, 11, 12),
                Set.of(1, 2, 3, 4, 5, 6),
                Set.of(),
                0,
                DRAW_DATE,
                false
        );
    }

    private void mockTicketResultAndWinningNumbers(TicketResultData ticketResult) {
        when(ticketExistenceChecker.ticketExistsById(any(UUID.class)))
                .thenReturn(true);
        when(ticketResultProvider.getResultForTicket(ticketResult.ticketId()))
                .thenReturn(Optional.of(ticketResult));
        when(idGenerator.generateId()).thenAnswer(inv -> UUID.randomUUID());

    }

    private void assertResultEquals(ResultResponse actual, TicketResultData expected) {
        assertAll(
                () -> assertThat(actual.getTicketId()).isEqualTo(expected.ticketId()),
                () -> assertThat(actual.getUserNumbers()).isEqualTo(expected.userNumbers()),
                () -> assertThat(actual.getWonNumbers()).isEqualTo(expected.wonNumbers()),
                () -> assertThat(actual.getHitNumbers()).isEqualTo(expected.hitNumbers()),
                () -> assertThat(actual.getHitCount()).isEqualTo(expected.hitCount()),
                () -> assertThat(actual.getDrawDate()).isEqualTo(expected.drawDate()),
                () -> assertThat(actual.isWinner()).isEqualTo(expected.isWinner())
        );
    }
}