package pl.pkasiewicz.lottogame.numbergenerator.application;

import org.junit.jupiter.api.Test;
import pl.pkasiewicz.lottogame.numbergenerator.domain.exception.InvalidNumberCountException;
import pl.pkasiewicz.lottogame.numbergenerator.domain.exception.NumberOutOfRangeException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WinningNumbersValidatorTest {

    private static final int EXPECTED_COUNT = 6;
    private static final int LOWER_BAND = 1;
    private static final int UPPER_BAND = 6;

    private final WinningNumbersValidator validator = new WinningNumbersValidator(
            new NumberGeneratorProperties(EXPECTED_COUNT, LOWER_BAND, UPPER_BAND)
    );

    @Test
    public void should_throw_exception_when_numbers_are_not_in_range() {
        // given
        Set<Integer> numbers = Set.of(0, 2, 3, 4, 5, 7);

        // when && then
        assertThatThrownBy(() -> validator.validate(numbers))
                .isInstanceOf(NumberOutOfRangeException.class)
                .hasMessageContaining("Numbers must be between 1 and 6");
    }

    @Test
    public void should_throw_exception_when_count_is_invalid() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5);

        // when & then
        assertThatThrownBy(() -> validator.validate(numbers))
                .isInstanceOf(InvalidNumberCountException.class)
                .hasMessageContaining("Expected 6 numbers, got 5");
    }

    @Test
    public void should_pass_validation_when_numbers_are_valid() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);

        // when & then
        assertThatCode(() -> validator.validate(numbers))
                .doesNotThrowAnyException();
    }
}