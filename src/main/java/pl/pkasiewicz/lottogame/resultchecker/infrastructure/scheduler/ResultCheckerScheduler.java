package pl.pkasiewicz.lottogame.resultchecker.infrastructure.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.pkasiewicz.lottogame.resultchecker.domain.ResultCheckerUseCase;

@Slf4j
@Component
@AllArgsConstructor
public class ResultCheckerScheduler {

    private final ResultCheckerUseCase resultChecker;

    @Scheduled(cron = "0 5 12 ? * SAT")
    public void checkResults() {
        log.info("Starting scheduled results checking");
        resultChecker.generateResults();
        log.info("Results checked successfully");
    }
}
