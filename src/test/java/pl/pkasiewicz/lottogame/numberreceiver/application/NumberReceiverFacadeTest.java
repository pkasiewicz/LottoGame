package pl.pkasiewicz.lottogame.numberreceiver.application;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pkasiewicz.lottogame.infrastructure.DrawDateGenerator;
import pl.pkasiewicz.lottogame.infrastructure.IdGenerator;
import pl.pkasiewicz.lottogame.numberreceiver.domain.Ticket;
import pl.pkasiewicz.lottogame.numberreceiver.domain.exception.InvalidTicketNumbersException;
import pl.pkasiewicz.lottogame.numberreceiver.domain.exception.InvalidTicketSizeException;
import pl.pkasiewicz.lottogame.numberreceiver.testhelpers.InMemoryTicketRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class NumberReceiverFacadeTest {

    private static final int EXPECTED_COUNT = 6;
    private static final int LOWER_BAND = 1;
    private static final int UPPER_BAND = 99;

    private NumberReceiverFacade numberReceiverFacade;

    @BeforeEach
    void setUp() {
        numberReceiverFacade = new NumberReceiverFacade(
                new NumberReceiverProperties(EXPECTED_COUNT, LOWER_BAND, UPPER_BAND),
                new InMemoryTicketRepository(),
                new IdGenerator(),
                new DrawDateGenerator(Clock.systemUTC())
        );
    }

    @Test
    public void should_save_numbers_to_db_when_user_gave_six_numbers_in_range() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);

        // when
        Ticket result = numberReceiverFacade.inputNumbers(numbers);

        // then
        assertThat(result.getNumbers()).isEqualTo(numbers);
        assertThat(result.getNumbers()).hasSize(6);
    }

    @Test
    public void should_throw_exception_when_user_gave_less_than_six_numbers() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5);

        // when
        Throwable thrown = catchThrowable(() -> numberReceiverFacade.inputNumbers(numbers));

        // then
        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(InvalidTicketSizeException.class)
                .hasMessage("Expected " + EXPECTED_COUNT + " numbers, got " + numbers.size());
    }

    @Test
    public void should_throw_exception_when_user_gave_more_than_six_numbers() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6, 7);

        // when
        Throwable thrown = catchThrowable(() -> numberReceiverFacade.inputNumbers(numbers));

        // then
        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(InvalidTicketSizeException.class)
                .hasMessage("Expected " + EXPECTED_COUNT + " numbers, got " + numbers.size());
    }

    @Test
    public void should_throw_exception_when_user_gave_at_least_one_number_out_of_range() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 2000);

        // when
        Throwable thrown = catchThrowable(() -> numberReceiverFacade.inputNumbers(numbers));

        // then
        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(InvalidTicketNumbersException.class)
                .hasMessage("Numbers must be between " + LOWER_BAND + " and " + UPPER_BAND);
    }

    @Test
    public void should_return_ticket_from_db() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);
        Ticket savedTicket = numberReceiverFacade.inputNumbers(numbers);

        // when
        List<Ticket> result = numberReceiverFacade.retrieveAllTicketsByNextDrawDate(savedTicket.getDrawDate());

        // then
        assertThat(result).contains(savedTicket);
    }

    @Test
    public void should_set_draw_date_to_next_saturday() {
        // given
        NumberReceiverFacade numberReceiverFacade = createFacadeWithFixedClock(2025, 10, 29, 10, 0);
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);

        // when
        Ticket result = numberReceiverFacade.inputNumbers(numbers);

        // then
        LocalDateTime expectedDrawDate = LocalDateTime.of(2025, 11, 1, 12, 0);
        assertThat(result.getDrawDate()).isEqualTo(expectedDrawDate);
    }

    @Test
    public void should_return_false_if_ticket_does_not_exists() {
        // given
        UUID nonExisingId = UUID.randomUUID();

        // when
        boolean result = numberReceiverFacade.ticketExists(nonExisingId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void should_return_true_if_ticket_exists() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);
        Ticket savedTicket = numberReceiverFacade.inputNumbers(numbers);

        // when
        boolean result = numberReceiverFacade.ticketExists(savedTicket.getId().value());

        // then
        assertThat(result).isTrue();
    }

    private NumberReceiverFacade createFacadeWithFixedClock(int year, int month, int day, int hour, int minute) {
        Clock fixedClock = Clock.fixed(
                LocalDateTime.of(year, month, day, hour, minute).toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );
        return new NumberReceiverFacade(
                new NumberReceiverProperties(EXPECTED_COUNT, LOWER_BAND, UPPER_BAND),
                new InMemoryTicketRepository(),
                new IdGenerator(),
                new DrawDateGenerator(fixedClock)
        );
    }
}