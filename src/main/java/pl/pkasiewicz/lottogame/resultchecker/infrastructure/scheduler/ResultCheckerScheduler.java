package pl.pkasiewicz.lottogame.resultchecker.infrastructure.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.pkasiewicz.lottogame.resultchecker.domain.port.ResultCheckerUseCase;

@Slf4j
@Component
@AllArgsConstructor
public class ResultCheckerScheduler {

    private final ResultCheckerUseCase resultChecker;

    @Scheduled(cron = "${lotto.result-checker.scheduler.cron}")
    public void checkResults() {
        log.info("Starting scheduled results checking");
        resultChecker.generateResults();
        log.info("Results checked successfully");
    }
}
