package pl.pkasiewicz.lottogame.numbergenerator.domain;

import java.util.Set;

public interface RandomNumbersGenerable {

    Set<Integer> generateRandomNumbers(int count, int min, int max);
}
