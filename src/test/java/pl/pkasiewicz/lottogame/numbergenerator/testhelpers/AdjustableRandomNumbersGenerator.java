package pl.pkasiewicz.lottogame.numbergenerator.testhelpers;

import pl.pkasiewicz.lottogame.numbergenerator.domain.port.RandomNumbersGeneratorPort;

import java.util.Set;

public class AdjustableRandomNumbersGenerator implements RandomNumbersGeneratorPort {

    private final Set<Integer> generatedNumbers;

    public AdjustableRandomNumbersGenerator() {
        generatedNumbers = Set.of(1, 2, 3, 4, 5, 6);
    }

    public AdjustableRandomNumbersGenerator(Set<Integer> generatedNumbers) {
        this.generatedNumbers = generatedNumbers;
    }

    @Override
    public Set<Integer> generateRandomNumbers(int count, int min, int max) {
        return generatedNumbers;
    }
}
