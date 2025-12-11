package pl.pkasiewicz.lottogame.numbergenerator.infrastructure.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbersGeneratorUseCase;

@Slf4j
@Component
@AllArgsConstructor
public class WinningNumbersScheduler {

    private final WinningNumbersGeneratorUseCase winningNumbersGenerator;

    @Scheduled(cron = "${lotto.number-generator.scheduler.cron}")
    public void generateWinningNumbers(){
        log.info("Starting scheduled winning numbers generation");
        winningNumbersGenerator.generateWinningNumbers();
        log.info("Winning numbers generated successfully");
    }
}
