package pl.pkasiewicz.lottogame.numbergenerator.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LocalRandomNumberGeneratorTest {

    private LocalRandomNumberGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new LocalRandomNumberGenerator();
    }

    @Test
    public void should_generate_six_unique_numbers() {
        // given && when
        Set<Integer> integers = generator.generateRandomNumbers(6, 1, 99);

        // then
        assertThat(integers.size()).isEqualTo(6);
    }

    @Test
    public void should_generate_numbers_within_bounds() {
        // given & when
        Set<Integer> numbers = generator.generateRandomNumbers(6, 10, 20);

        // then
        assertThat(numbers)
                .hasSize(6)
                .allSatisfy(number -> assertThat(number).isBetween(10, 20));
    }

    @Test
    public void should_handle_minimal_range() {
        // given && when
        Set<Integer> numbers = generator.generateRandomNumbers(2, 1, 2);

        // then
        assertThat(numbers).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    public void should_generate_different_results_on_multiple_calls() {
        // given && when
        Set<Integer> first = generator.generateRandomNumbers(6, 1, 49);
        Set<Integer> second = generator.generateRandomNumbers(6, 1, 49);

        // then
        assertThat(first).isNotEqualTo(second);
    }
}