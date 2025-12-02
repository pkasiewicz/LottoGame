package pl.pkasiewicz.lottogame.numbergenerator.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.pkasiewicz.lottogame.numbergenerator.domain.RandomNumberGeneratorPort;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class RandomNumberApiAdapter implements RandomNumberGeneratorPort {

    private final RestTemplate restTemplate;
    private final RandomNumberGenerator fallback;

    public RandomNumberApiAdapter(RestTemplate restTemplate,
                                  @Qualifier("randomNumberGenerator") RandomNumberGenerator fallback) {
        this.restTemplate = restTemplate;
        this.fallback = fallback;
    }

    @Override
    public Set<Integer> generateRandomNumbers(int count, int min, int max) {
        try {
            String url = String.format("https://www.randomnumberapi.com/api/v1.0/random?min=%d&max=%d&count=%d", min, max, count);
            Integer[] numbers = restTemplate.getForObject(url, Integer[].class);
            if (numbers == null) {
                log.warn("API returned null, using fallback generator");
                return  fallback.generateRandomNumbers(count, min, max);
            }
            return new HashSet<>(Arrays.asList(numbers));
        } catch (Exception e) {
            log.warn("Failed to fetch from API, using fallback generator", e);
            return fallback.generateRandomNumbers(count, min, max);
        }
    }
}
