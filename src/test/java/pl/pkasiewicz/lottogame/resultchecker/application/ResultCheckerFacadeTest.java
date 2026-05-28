package pl.pkasiewicz.lottogame.resultchecker.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pkasiewicz.lottogame.domain.port.DrawDateGenerable;
import pl.pkasiewicz.lottogame.domain.port.IdGenerable;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketData;
import pl.pkasiewicz.lottogame.resultchecker.domain.port.TicketProvider;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResult;
import pl.pkasiewicz.lottogame.domain.port.WinningNumbersProvider;
import pl.pkasiewicz.lottogame.resultchecker.testhelpers.InMemoryTicketResultRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultCheckerFacadeTest {

    public static final LocalDateTime DRAW_DATE = LocalDateTime.of(2025, 11, 1, 12, 0);
    public static final Set<Integer> WINNING_NUMBERS = Set.of(1, 2, 3, 4, 5, 6);

    private ResultCheckerFacade resultCheckerFacade;
    private InMemoryTicketResultRepository repository;
    private WinningNumbersProvider winningNumbersProvider;
    private TicketProvider ticketProvider;
    private DrawDateGenerable drawDateGenerator;
    private IdGenerable idGenerator;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTicketResultRepository();
        winningNumbersProvider = mock(WinningNumbersProvider.class);
        ticketProvider = mock(TicketProvider.class);
        drawDateGenerator = mock(DrawDateGenerable.class);
        idGenerator = mock(IdGenerable.class);

        resultCheckerFacade = new ResultCheckerFacade(
                repository,
                winningNumbersProvider,
                ticketProvider,
                drawDateGenerator,
                idGenerator
        );
    }

    @Test
    public void should_return_winning_ticket_when_user_hit_all_six_numbers() {
        // given
        when(drawDateGenerator.getNextDrawDate()).thenReturn(DRAW_DATE);
        when(idGenerator.generateId()).thenReturn(UUID.randomUUID());

        mockWinningNumbers();
        mockSingleTicket(Set.of(1, 2, 3, 4, 5, 6));

        // when
        List<TicketResult> results = resultCheckerFacade.generateResults();

        // then
        assertThat(results).hasSize(1);

        TicketResult ticketResult = results.get(0);
        assertAll(
                () -> assertThat(ticketResult.isWinner()).isTrue(),
                () -> assertThat(ticketResult.getDrawDate()).isEqualTo(DRAW_DATE),
                () -> assertThat(ticketResult.getHitCount()).isEqualTo(6),
                () -> assertThat(ticketResult.getHitNumbers()).containsExactlyInAnyOrder(1, 2, 3, 4, 5, 6)
        );
    }

    @Test
    public void should_return_non_winning_ticket_when_user_hit_less_than_three_numbers() {
        // given
        when(drawDateGenerator.getNextDrawDate()).thenReturn(DRAW_DATE);
        when(idGenerator.generateId()).thenReturn(UUID.randomUUID());

        mockWinningNumbers();
        mockSingleTicket(Set.of(7, 8, 9, 10, 11, 12));

        // when
        List<TicketResult> results = resultCheckerFacade.generateResults();

        // then
        assertThat(results).hasSize(1);

        TicketResult ticketResult = results.get(0);
        assertAll(
                () -> assertThat(ticketResult.isWinner()).isFalse(),
                () -> assertThat(ticketResult.getDrawDate()).isEqualTo(DRAW_DATE),
                () -> assertThat(ticketResult.getHitCount()).isEqualTo(0),
                () -> assertThat(ticketResult.getHitNumbers()).isEmpty()
        );
    }

    @Test
    public void should_return_winning_ticket_when_user_hit_three_numbers() {
        // given
        when(drawDateGenerator.getNextDrawDate()).thenReturn(DRAW_DATE);
        when(idGenerator.generateId()).thenReturn(UUID.randomUUID());

        mockWinningNumbers();
        mockSingleTicket(Set.of(4, 5, 6, 7, 8, 9));

        // when
        List<TicketResult> results = resultCheckerFacade.generateResults();

        // then
        assertThat(results).hasSize(1);

        TicketResult ticketResult = results.get(0);
        assertAll(
                () -> assertThat(ticketResult.isWinner()).isTrue(),
                () -> assertThat(ticketResult.getDrawDate()).isEqualTo(DRAW_DATE),
                () -> assertThat(ticketResult.getHitCount()).isEqualTo(3),
                () -> assertThat(ticketResult.getHitNumbers()).containsExactlyInAnyOrder(4, 5, 6)
        );
    }

    @Test
    public void should_calculate_correct_hit_count_for_multiple_tickets() {
        // given
        when(drawDateGenerator.getNextDrawDate()).thenReturn(DRAW_DATE);
        when(idGenerator.generateId()).thenReturn(UUID.randomUUID());

        mockWinningNumbers();
        mockTickets(List.of(
                        Set.of(1, 2, 3, 4, 5, 6),   // 6 trafień
                        Set.of(4, 5, 6, 7, 8, 9),   // 3 trafienia
                        Set.of(7, 8, 9, 10, 11, 12) // 0 trafień
                )
        );

        // when
        List<TicketResult> results = resultCheckerFacade.generateResults();

        // then
        assertThat(results).hasSize(3);

        assertThat(results)
                .extracting(TicketResult::getHitCount)
                .containsExactlyInAnyOrder(6, 3, 0);

        assertThat(results)
                .extracting(TicketResult::isWinner)
                .containsExactlyInAnyOrder(true, true, false);
    }

    @Test
    public void should_save_all_ticket_results_to_repository() {
        // given
        when(drawDateGenerator.getNextDrawDate()).thenReturn(DRAW_DATE);
        when(idGenerator.generateId()).thenAnswer(inv -> UUID.randomUUID());

        mockWinningNumbers();
        mockTickets(List.of(
                        Set.of(1, 2, 3, 4, 5, 6),   // 6 trafień
                        Set.of(4, 5, 6, 7, 8, 9),   // 3 trafienia
                        Set.of(7, 8, 9, 10, 11, 12) // 0 trafień
                )
        );

        // when
        resultCheckerFacade.generateResults();

        // then
        List<TicketResult> savedTicketResults = repository.findAll();
        assertThat(savedTicketResults).hasSize(3);

        assertThat(savedTicketResults).extracting(TicketResult::getHitCount)
                .containsExactlyInAnyOrder(6, 3, 0);
    }

    @Test
    public void should_return_empty_list_when_no_tickets_for_draw_date() {
        // given
        when(drawDateGenerator.getNextDrawDate()).thenReturn(DRAW_DATE);
        when(ticketProvider.getTicketsByDrawDate(DRAW_DATE)).thenReturn(List.of());

        mockWinningNumbers();

        // when
        List<TicketResult> results = resultCheckerFacade.generateResults();

        // then
        assertThat(results).isEmpty();
    }

    @Test
    public void should_retrieve_ticket_result_from_db() {
        // given
        when(drawDateGenerator.getNextDrawDate()).thenReturn(DRAW_DATE);
        when(idGenerator.generateId()).thenReturn(UUID.randomUUID());

        mockWinningNumbers();
        mockSingleTicket(Set.of(1, 2, 3, 4, 5, 6));

        List<TicketResult> savedTicketResult = resultCheckerFacade.generateResults();
        UUID ticketId = savedTicketResult.get(0).getTicketId();

        // when
        TicketResult result = resultCheckerFacade.getResultForTicket(ticketId).get();

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.isWinner()).isTrue(),
                () -> assertThat(result.getDrawDate()).isEqualTo(DRAW_DATE),
                () -> assertThat(result.getHitCount()).isEqualTo(6),
                () -> assertThat(result.getHitNumbers()).containsExactlyInAnyOrder(1, 2, 3, 4, 5, 6)
        );
    }

    @Test
    public void should_return_optional_empty_when_ticket_result_not_found() {
        // given
        UUID randomTicketId = UUID.randomUUID();

        // when
        Optional<TicketResult> resultForTicket = resultCheckerFacade.getResultForTicket(randomTicketId);

        // then
        assertThat(resultForTicket).isEmpty();
    }

    @Test
    public void should_return_correct_draw_date_for_existing_ticket() {
        // given
        when(drawDateGenerator.getNextDrawDate()).thenReturn(DRAW_DATE);
        when(idGenerator.generateId()).thenReturn(UUID.randomUUID());

        mockWinningNumbers();
        mockSingleTicket(Set.of(1, 2, 3, 4, 5, 6));

        List<TicketResult> savedTicketResult = resultCheckerFacade.generateResults();
        UUID ticketId = savedTicketResult.get(0).getTicketId();

        // when
        TicketResult resultForTicket = resultCheckerFacade.getResultForTicket(ticketId).get();
        LocalDateTime drawDateForTicket = resultForTicket.getDrawDate();

        // then
        assertThat(drawDateForTicket).isEqualTo(DRAW_DATE);
    }

    @Test
    public void should_return_correct_draw_date_when_multiple_tickets_exist() {
        // given
        when(drawDateGenerator.getNextDrawDate()).thenReturn(DRAW_DATE);
        when(idGenerator.generateId()).thenAnswer(inv -> UUID.randomUUID());

        mockWinningNumbers();
        mockTickets(List.of(
                        Set.of(1, 2, 3, 4, 5, 6),
                        Set.of(4, 5, 6, 7, 8, 9),
                        Set.of(7, 8, 9, 10, 11, 12)
                )
        );

        List<TicketResult> savedTicketResult = resultCheckerFacade.generateResults();
        UUID ticketId = savedTicketResult.get(0).getTicketId();

        // when
        TicketResult resultForTicket = resultCheckerFacade.getResultForTicket(ticketId).get();
        LocalDateTime drawDateForTicket = resultForTicket.getDrawDate();

        // then
        assertAll(
                () -> assertThat(drawDateForTicket).isEqualTo(DRAW_DATE),
                () -> assertThat(savedTicketResult.get(1).getTicketId()).isNotEqualTo(ticketId)
        );
    }

    private void mockWinningNumbers() {
        when(winningNumbersProvider.getWinningNumbersByDate(DRAW_DATE)).thenReturn(WINNING_NUMBERS);
        when(winningNumbersProvider.areWinningNumbersGeneratedByDate()).thenReturn(true);
    }

    private void mockSingleTicket(Set<Integer> numbers) {
        TicketData ticketData = new TicketData(
                UUID.randomUUID(),
                numbers,
                DRAW_DATE
        );

        when(ticketProvider.getTicketsByDrawDate(DRAW_DATE)).thenReturn(List.of(ticketData));
    }

    private void mockTickets(List<Set<Integer>> listOfNumbers) {
        List<TicketData> ticketsData = listOfNumbers.stream()
                .map(numbers -> new TicketData(
                        UUID.randomUUID(),
                        numbers,
                        DRAW_DATE))
                .toList();

        when(ticketProvider.getTicketsByDrawDate(DRAW_DATE)).thenReturn(ticketsData);
    }
}