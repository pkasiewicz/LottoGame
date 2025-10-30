package pl.pkasiewicz.lottogame.numbergenerator.application;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pkasiewicz.lottogame.infrastructure.IdGenerator;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbers;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbersId;
import pl.pkasiewicz.lottogame.numbergenerator.domain.exception.WinningNumbersNotFoundException;
import pl.pkasiewicz.lottogame.numbergenerator.testhelpers.AdjustableRandomNumbersGenerator;
import pl.pkasiewicz.lottogame.numbergenerator.testhelpers.InMemoryWinningNumbersRepository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class NumberGeneratorFacadeTest {

    private static final int EXPECTED_COUNT = 6;
    private static final int LOWER_BAND = 1;
    private static final int UPPER_BAND = 99;

    private final NumberGeneratorFacade numberGeneratorFacade = new NumberGeneratorFacade(
            new AdjustableRandomNumbersGenerator(),
            new NumberGeneratorProperties(EXPECTED_COUNT, LOWER_BAND, UPPER_BAND),
            new InMemoryWinningNumbersRepository(),
            new IdGenerator()
    );


    @Test
    public void should_generate_valid_random_numbers() {
        // given
        Set<Integer> expected = Set.of(1, 2, 3, 4, 5, 6);

        // when
        WinningNumbers result = numberGeneratorFacade.generateWinningNumbers();

        // then
        assertThat(result.getWinningNumbers()).isEqualTo(expected);
        assertThat(result.getWinningNumbers()).hasSize(6);
    }

    @Test
    public void should_return_existing_numbers_when_date_already_exists() {
        // given
        LocalDateTime date = LocalDateTime.now();
        WinningNumbers expected = new WinningNumbers(
                new WinningNumbersId(UUID.randomUUID()),
                Set.of(1, 2, 3, 4, 5, 6),
                date
        );

        // when
        WinningNumbers result = numberGeneratorFacade.generateWinningNumbers();

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void should_retrieve_winning_numbers_by_date() {
        // given
        LocalDateTime date = LocalDateTime.now();
        WinningNumbers expected = new WinningNumbers(
                new WinningNumbersId(UUID.randomUUID()),
                Set.of(1, 2, 3, 4, 5, 6),
                date
        );

        // when
        WinningNumbers actual = numberGeneratorFacade.retrieveWinningNumbersByDate(date);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void should_throw_exception_when_numbers_not_found_for_date() {
        // given
        LocalDateTime date = LocalDateTime.now();

        // when
        Throwable thrown = catchThrowable(numberGeneratorFacade::generateWinningNumbers);

        // then
        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(WinningNumbersNotFoundException.class)
                .hasMessage("No number found for date: " + date);
    }
}