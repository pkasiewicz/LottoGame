package pl.pkasiewicz.lottogame.numbergenerator.application;

import org.springframework.stereotype.Service;
import pl.pkasiewicz.lottogame.domain.DrawDateGenerable;
import pl.pkasiewicz.lottogame.domain.IdGenerable;
import pl.pkasiewicz.lottogame.numbergenerator.domain.*;
import pl.pkasiewicz.lottogame.numbergenerator.domain.exception.WinningNumbersNotFoundException;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class WinningNumbersGeneratorFacade implements WinningNumbersGeneratorUseCase {

    private final RandomNumberGeneratorPort randomNumbersGenerator;
    private final WinningNumbersGeneratorProperties properties;
    private final WinningNumbersRepository repository;
    private final IdGenerable idGenerator;
    private final WinningNumbersValidator winningNumbersValidator;
    private final DrawDateGenerable drawDateGenerator;

    public WinningNumbersGeneratorFacade(RandomNumberGeneratorPort randomNumbersGenerator,
                                         WinningNumbersGeneratorProperties properties,
                                         WinningNumbersRepository repository,
                                         IdGenerable idGenerator,
                                         DrawDateGenerable drawDateGenerator) {
        this.randomNumbersGenerator = randomNumbersGenerator;
        this.properties = properties;
        this.repository = repository;
        this.idGenerator = idGenerator;
        this.winningNumbersValidator = new WinningNumbersValidator(properties);
        this.drawDateGenerator = drawDateGenerator;
    }

    @Override
    public WinningNumbers generateWinningNumbers() {
        LocalDateTime nextDrawDate = drawDateGenerator.getNextDrawDate();

        return repository.findByDate(nextDrawDate)
                .orElseGet(() -> generateAndSaveNewNumbers(nextDrawDate));
    }

    @Override
    public WinningNumbers retrieveWinningNumbersByDate(LocalDateTime date) {
        return repository.findByDate(date)
                .orElseThrow(() -> new WinningNumbersNotFoundException("Winning numbers for date: " + date + " not found"));
    }

    @Override
    public boolean areWinningNumbersGeneratedByDate() {
        LocalDateTime nextDrawDate = drawDateGenerator.getNextDrawDate();
        return repository.existsByDate(nextDrawDate);
    }

    private WinningNumbers generateAndSaveNewNumbers(LocalDateTime drawDate) {
        Set<Integer> randomNumbers = randomNumbersGenerator
                .generateRandomNumbers(properties.count(), properties.lowerBand(), properties.upperBand());
        winningNumbersValidator.validate(randomNumbers);

        WinningNumbers winningNumbers = new WinningNumbers(
                new WinningNumbersId(idGenerator.generateId()),
                randomNumbers,
                drawDate
        );

        return repository.save(winningNumbers);
    }
}
