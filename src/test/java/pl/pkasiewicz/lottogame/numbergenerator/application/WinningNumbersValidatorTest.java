package pl.pkasiewicz.lottogame.numbergenerator.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WinningNumbersValidatorTest {

    public static final int EXPECTED_COUNT = 6;
    public static final int LOWER_BAND = 1;
    public static final int UPPER_BAND = 6;

    private WinningNumbersValidator validator;

    @BeforeEach
    void setUp() {
        NumberGeneratorProperties properties = new NumberGeneratorProperties(EXPECTED_COUNT, LOWER_BAND, UPPER_BAND);
        this.validator = new WinningNumbersValidator(properties);
    }

    @Test
    public void should_throw_exception_when_numbers_are_not_in_range() {
        // given
        Set<Integer> numbers = Set.of(0, 2, 3, 4, 5, 7);

        // when && then
        assertThatThrownBy(() -> validator.validate(numbers))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Numbers must be between 1 and 6");
    }

    @Test
    public void should_throw_exception_when_count_is_invalid() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5);

        // when & then
        assertThatThrownBy(() -> validator.validate(numbers))
                .isInstanceOf(IllegalArgumentException.class)
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