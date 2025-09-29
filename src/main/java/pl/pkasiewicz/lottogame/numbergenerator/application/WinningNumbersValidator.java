package pl.pkasiewicz.lottogame.numbergenerator.application;

import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
class WinningNumbersValidator {

    private final NumberGeneratorProperties properties;

    public void validate(Set<Integer> numbers) {
        validateCount(numbers);
        validateRange(numbers);
    }

    private void validateCount(Set<Integer> numbers) {
        if (numbers.size() != properties.count()) {
            throw new IllegalArgumentException("Expected " + properties.count() + " numbers, got " + numbers.size());
        }
    }

    private void validateRange(Set<Integer> numbers) {
        boolean hasInvalidNumbers = numbers.stream()
                .anyMatch(number -> number < properties.lowerBand() || number > properties.upperBand());

        if (hasInvalidNumbers) {
            throw new IllegalArgumentException("Numbers must be between " + properties.lowerBand() + " and " + properties.upperBand());
        }
    }
}
