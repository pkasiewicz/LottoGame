package pl.pkasiewicz.lottogame.numbergenerator.domain;

import java.util.Set;

public interface RandomNumbersGeneratorPort {

    Set<Integer> generateRandomNumbers(int count, int min, int max);
}
