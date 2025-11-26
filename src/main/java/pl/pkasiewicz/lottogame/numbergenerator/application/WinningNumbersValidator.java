package pl.pkasiewicz.lottogame.numbergenerator.application;

import lombok.RequiredArgsConstructor;
import pl.pkasiewicz.lottogame.numbergenerator.domain.exception.InvalidNumberCountException;
import pl.pkasiewicz.lottogame.numbergenerator.domain.exception.NumberOutOfRangeException;

import java.util.Set;

@RequiredArgsConstructor
class WinningNumbersValidator {

    private final WinningNumbersGeneratorProperties properties;

    public void validate(Set<Integer> numbers) {
        validateCount(numbers);
        validateRange(numbers);
    }

    private void validateCount(Set<Integer> numbers) {
        if (numbers.size() != properties.count()) {
            throw new InvalidNumberCountException("Expected " + properties.count() + " numbers, got " + numbers.size());
        }
    }

    private void validateRange(Set<Integer> numbers) {
        boolean hasInvalidNumbers = numbers.stream()
                .anyMatch(number -> number < properties.lowerBand() || number > properties.upperBand());

        if (hasInvalidNumbers) {
            throw new NumberOutOfRangeException("Numbers must be between " + properties.lowerBand() + " and " + properties.upperBand());
        }
    }
}
