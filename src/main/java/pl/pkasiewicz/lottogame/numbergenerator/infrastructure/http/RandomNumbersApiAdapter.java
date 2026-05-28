package pl.pkasiewicz.lottogame.numbergenerator.infrastructure.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.pkasiewicz.lottogame.numbergenerator.domain.port.RandomNumbersGeneratorPort;
import pl.pkasiewicz.lottogame.numbergenerator.infrastructure.adapter.RandomNumbersGenerator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class RandomNumbersApiAdapter implements RandomNumbersGeneratorPort {

    private final RestTemplate restTemplate;
    private final RandomNumbersGenerator fallback;

    private static final int MAX_RETRIES = 3;
    private final String apiUrl;

    public RandomNumbersApiAdapter(RestTemplate restTemplate,
                                   @Qualifier("randomNumberGenerator") RandomNumbersGenerator fallback,
                                   @Value("${lotto.number-generator.api.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.fallback = fallback;
        this.apiUrl = apiUrl;
    }

    @Override
    public Set<Integer> generateRandomNumbers(int count, int min, int max) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                Set<Integer> numbers = fetchNumbersFromApi(count, min, max);

                if (numbers.size() == count) {
                    return numbers;
                }

                log.warn("API returned {} unique numbers, expected {}. Attempt {}/{}",
                        numbers.size(), count, attempt, MAX_RETRIES);

            } catch (Exception e) {
                log.warn("Failed to fetch from API on attempt {}/{}: {}",
                        attempt, MAX_RETRIES, e.getMessage());
            }
        }

        log.warn("All API attempts failed, using fallback generator");
        return fallback.generateRandomNumbers(count, min, max);
    }

    private Set<Integer> fetchNumbersFromApi(int count, int min, int max) {
        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("min", min)
                .queryParam("max", max)
                .queryParam("count", count)
                .toUriString();

        Integer[] numbers = restTemplate.getForObject(url, Integer[].class);

        if (numbers == null) {
            throw new IllegalStateException("API returned null");
        }

        return new HashSet<>(Arrays.asList(numbers));
    }
}
