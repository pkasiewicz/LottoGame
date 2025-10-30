package pl.pkasiewicz.lottogame.numberreceiver.application;

import lombok.AllArgsConstructor;
import pl.pkasiewicz.lottogame.numberreceiver.domain.exception.InvalidTicketSizeException;
import pl.pkasiewicz.lottogame.numberreceiver.domain.exception.InvalidTicketNumbersException;

import java.util.Set;

@AllArgsConstructor
class NumberReceiverValidator {

    private final NumberReceiverProperties properties;

    public void validate(Set<Integer> numbers) {
        validateCount(numbers);
        validateRange(numbers);
    }

    private void validateCount(Set<Integer> numbers) {
        if (numbers.size() != properties.count()) {
            throw new InvalidTicketSizeException("Expected " + properties.count() + " numbers, got " + numbers.size());
        }
    }

    private void validateRange(Set<Integer> numbers) {
        boolean hasInvalidNumbers = numbers.stream()
                .anyMatch(number -> number < properties.lowerBand() || number > properties.upperBand());

        if (hasInvalidNumbers) {
            throw new InvalidTicketNumbersException("Numbers must be between " + properties.lowerBand() + " and " + properties.upperBand());
        }
    }
}
