package pl.pkasiewicz.lottogame.numbergenerator.application;

import org.springframework.stereotype.Service;
import pl.pkasiewicz.lottogame.numbergenerator.domain.*;
import pl.pkasiewicz.lottogame.numbergenerator.domain.excpetion.WinningNumbersNotFoundException;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class NumberGeneratorService implements NumberGeneratorUseCase {

    private final RandomNumbersGenerable randomNumbersGenerator;
    private final NumberGeneratorProperties properties;
    private final WinningNumbersRepository repository;
    private final WinningNumbersValidator winningNumbersValidator;

    public NumberGeneratorService(RandomNumbersGenerable randomNumbersGenerator,
                                  NumberGeneratorProperties properties,
                                  WinningNumbersRepository repository) {
        this.randomNumbersGenerator = randomNumbersGenerator;
        this.properties = properties;
        this.repository = repository;
        this.winningNumbersValidator = new WinningNumbersValidator(properties);
    }

    @Override
    public WinningNumbers generateWinningNumbers() {
        LocalDateTime today = LocalDateTime.now(); //todo

        if (repository.existsByDate(today)) {
            return repository.findByDate(today).orElseThrow();
        }

        Set<Integer> randomNumbers = randomNumbersGenerator
                .generateRandomNumbers(properties.count(), properties.lowerBand(), properties.upperBand());
        winningNumbersValidator.validate(randomNumbers);
        return repository.save(
                new WinningNumbers(
                        new WinningNumbersId(UUID.randomUUID()),
                        randomNumbers,
                        today)
        );
    }

    @Override
    public WinningNumbers retrieveWinningNumbersByDate(LocalDateTime date) {
        return repository.findByDate(date)
                .orElseThrow(() -> new WinningNumbersNotFoundException("Winning numbers not found"));
    }
}
