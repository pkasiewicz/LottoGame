package pl.pkasiewicz.lottogame.numbergenerator.application;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pkasiewicz.lottogame.infrastructure.DrawDateGenerator;
import pl.pkasiewicz.lottogame.infrastructure.IdGenerator;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbers;
import pl.pkasiewicz.lottogame.numbergenerator.domain.exception.WinningNumbersNotFoundException;
import pl.pkasiewicz.lottogame.numbergenerator.testhelpers.AdjustableRandomNumbersGenerator;
import pl.pkasiewicz.lottogame.numbergenerator.testhelpers.InMemoryWinningNumbersRepository;

import java.time.*;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class WinningNumbersGeneratorFacadeTest {

    private static final int EXPECTED_COUNT = 6;
    private static final int LOWER_BAND = 1;
    private static final int UPPER_BAND = 99;

    private WinningNumbersGeneratorFacade winningNumbersGeneratorFacade;

    @BeforeEach
    void setUp() {
        winningNumbersGeneratorFacade = new WinningNumbersGeneratorFacade(
                new AdjustableRandomNumbersGenerator(),
                new WinningNumbersGeneratorProperties(EXPECTED_COUNT, LOWER_BAND, UPPER_BAND),
                new InMemoryWinningNumbersRepository(),
                new IdGenerator(),
                new DrawDateGenerator(Clock.systemUTC())
        );
    }

    @Test
    public void should_generate_valid_random_numbers() {
        // given
        Set<Integer> expected = Set.of(1, 2, 3, 4, 5, 6);

        // when
        WinningNumbers result = winningNumbersGeneratorFacade.generateWinningNumbers();

        // then
        assertThat(result.getWinningNumbers()).isEqualTo(expected);
        assertThat(result.getWinningNumbers()).hasSize(6);
    }

    @Test
    public void should_return_existing_numbers_when_date_already_exists() {
        // given
        WinningNumbers existingNumbers = winningNumbersGeneratorFacade.generateWinningNumbers();

        // when
        WinningNumbers result = winningNumbersGeneratorFacade.generateWinningNumbers();

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(existingNumbers);
    }

    @Test
    public void should_retrieve_winning_numbers_by_date() {
        // given
        WinningNumbersGeneratorFacade winningNumbersGeneratorFacade = createFacadeWithFixedClock(2025, 10, 29, 10, 0);
        winningNumbersGeneratorFacade.generateWinningNumbers();

        // when
        LocalDateTime expectedDrawDate = LocalDateTime.of(2025, 11, 1, 12, 0);
        WinningNumbers result = winningNumbersGeneratorFacade.retrieveWinningNumbersByDate(expectedDrawDate);

        // then
        assertThat(result.getDrawDate()).isEqualTo(expectedDrawDate);
        assertThat(result.getWinningNumbers()).hasSize(EXPECTED_COUNT);
    }

    @Test
    void should_throw_exception_when_numbers_not_found_for_date() {
        // given
        LocalDateTime nonExistentDate = LocalDateTime.of(2020, 1, 1, 12, 0);

        // when
        Throwable thrown = catchThrowable(() -> winningNumbersGeneratorFacade.retrieveWinningNumbersByDate(nonExistentDate));

        // then
        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(WinningNumbersNotFoundException.class)
                .hasMessage("Winning numbers for date: " + nonExistentDate + " not found");
    }

    @Test
    void should_return_true_when_winning_numbers_generated_for_next_draw_date() {
        // given
        WinningNumbersGeneratorFacade winningNumbersGeneratorFacade = createFacadeWithFixedClock(2025, 10, 29, 10, 0);
        winningNumbersGeneratorFacade.generateWinningNumbers();

        // when
        boolean result = winningNumbersGeneratorFacade.areWinningNumbersGeneratedByDate();

        // then
        assertThat(result).isTrue();
    }

    @Test
    void should_return_false_when_winning_numbers_not_generated_for_next_draw_date() {
        // given && when
        boolean result = winningNumbersGeneratorFacade.areWinningNumbersGeneratedByDate();

        // then
        assertThat(result).isFalse();
    }

    private WinningNumbersGeneratorFacade createFacadeWithFixedClock(int year, int month, int day, int hour, int minute) {
        Clock fixedClock = Clock.fixed(
                LocalDateTime.of(year, month, day, hour, minute).toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );
        return new WinningNumbersGeneratorFacade(
                new AdjustableRandomNumbersGenerator(),
                new WinningNumbersGeneratorProperties(EXPECTED_COUNT, LOWER_BAND, UPPER_BAND),
                new InMemoryWinningNumbersRepository(),
                new IdGenerator(),
                new DrawDateGenerator(fixedClock)
        );
    }
}