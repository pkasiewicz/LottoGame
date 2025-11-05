package pl.pkasiewicz.lottogame.resultchecker.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pkasiewicz.lottogame.infrastructure.DrawDateGenerator;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbers;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbersGeneratorUseCase;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbersId;
import pl.pkasiewicz.lottogame.numberreceiver.domain.NumberReceiverUseCase;
import pl.pkasiewicz.lottogame.numberreceiver.domain.Ticket;
import pl.pkasiewicz.lottogame.numberreceiver.domain.TicketId;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResult;
import pl.pkasiewicz.lottogame.resultchecker.testhelpers.InMemoryTicketResultRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultCheckerFacadeTest {

    public static final LocalDateTime DRAW_DATE = LocalDateTime.of(2025, 11, 1, 12, 0);

    private ResultCheckerFacade resultCheckerFacade;
    private InMemoryTicketResultRepository repository;
    private WinningNumbersGeneratorUseCase numberGenerator;
    private NumberReceiverUseCase numberReceiver;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTicketResultRepository();
        numberGenerator = mock(WinningNumbersGeneratorUseCase.class);
        numberReceiver = mock(NumberReceiverUseCase.class);
        DrawDateGenerator drawDateGenerator = mock(DrawDateGenerator.class);

        resultCheckerFacade = new ResultCheckerFacade(
                repository,
                numberGenerator,
                numberReceiver,
                drawDateGenerator
        );

        when(drawDateGenerator.getNextDrawDate()).thenReturn(DRAW_DATE);
    }

    @Test
    public void should_return_winning_ticket_when_user_hit_all_six_numbers() {
        // given
        mockWinningNumbers();
        mockSingleTicket(Set.of(1, 2, 3, 4, 5, 6));

        // when
        List<TicketResult> results = resultCheckerFacade.checkResults();

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
        mockWinningNumbers();
        mockSingleTicket(Set.of(7, 8, 9, 10, 11, 12));

        // when
        List<TicketResult> results = resultCheckerFacade.checkResults();

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
        mockWinningNumbers();
        mockSingleTicket(Set.of(4, 5, 6, 7, 8, 9));

        // when
        List<TicketResult> results = resultCheckerFacade.checkResults();

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
        mockWinningNumbers();
        mockTickets(List.of(
                        Set.of(1, 2, 3, 4, 5, 6),   // 6 trafień
                        Set.of(4, 5, 6, 7, 8, 9),   // 3 trafienia
                        Set.of(7, 8, 9, 10, 11, 12) // 0 trafień
                )
        );

        // when
        List<TicketResult> results = resultCheckerFacade.checkResults();

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
        mockWinningNumbers();
        mockTickets(List.of(
                        Set.of(1, 2, 3, 4, 5, 6),   // 6 trafień
                        Set.of(4, 5, 6, 7, 8, 9),   // 3 trafienia
                        Set.of(7, 8, 9, 10, 11, 12) // 0 trafień
                )
        );

        // when
        resultCheckerFacade.checkResults();

        // then
        List<TicketResult> savedTicketResults = repository.findAll();
        assertThat(savedTicketResults).hasSize(3);

        assertThat(savedTicketResults).extracting(TicketResult::getHitCount)
                .containsExactlyInAnyOrder(6, 3, 0);
    }

    @Test
    public void should_return_empty_list_when_no_tickets_for_draw_date() {
        // given
        mockWinningNumbers();

        when(numberReceiver.retrieveAllTicketsByNextDrawDate(DRAW_DATE)).thenReturn(List.of());

        // when
        List<TicketResult> results = resultCheckerFacade.checkResults();

        // then
        assertThat(results).isEmpty();
    }

    private void mockWinningNumbers() {
        when(numberGenerator.retrieveWinningNumbersByDate(DRAW_DATE))
                .thenReturn(new WinningNumbers(
                        new WinningNumbersId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                        Set.of(1, 2, 3, 4, 5, 6),
                        DRAW_DATE)
                );
    }

    private void mockSingleTicket(Set<Integer> numbers) {
        Ticket ticket = new Ticket(
                new TicketId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                numbers,
                DRAW_DATE
        );

        when(numberReceiver.retrieveAllTicketsByNextDrawDate(DRAW_DATE)).thenReturn(List.of(ticket));
    }

    private void mockTickets(List<Set<Integer>> listOfNumbers) {
        List<Ticket> tickets = listOfNumbers.stream()
                .map(numbers -> new Ticket(
                        new TicketId(UUID.randomUUID()),
                        numbers,
                        DRAW_DATE))
                .toList();

        when(numberReceiver.retrieveAllTicketsByNextDrawDate(DRAW_DATE)).thenReturn(tickets);
    }
}