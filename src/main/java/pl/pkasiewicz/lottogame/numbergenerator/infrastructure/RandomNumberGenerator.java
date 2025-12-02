package pl.pkasiewicz.lottogame.numbergenerator.infrastructure;

import org.springframework.stereotype.Component;
import pl.pkasiewicz.lottogame.numbergenerator.domain.RandomNumberGeneratorPort;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Component("randomNumberGenerator")
public class RandomNumberGenerator implements RandomNumberGeneratorPort {

    private final Random random = new Random();

    @Override
    public Set<Integer> generateRandomNumbers(int count, int min, int max) {
        Set<Integer> numbers = new HashSet<>();
        while (numbers.size() < count) {
            numbers.add(random.nextInt(max - min + 1) + min);
        }
        return numbers;
    }
}
