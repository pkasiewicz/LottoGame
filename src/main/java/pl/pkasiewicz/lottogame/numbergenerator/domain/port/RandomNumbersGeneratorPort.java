package pl.pkasiewicz.lottogame.numbergenerator.domain.port;

import java.util.Set;

public interface RandomNumbersGeneratorPort {

    Set<Integer> generateRandomNumbers(int count, int min, int max);
}
