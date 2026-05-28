package pl.pkasiewicz.lottogame.domain.port;

import pl.pkasiewicz.lottogame.resultannouncer.application.ResultAnnouncerFacade;
import pl.pkasiewicz.lottogame.resultchecker.application.ResultCheckerFacade;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Port for retrieving winning numbers based on the draw date.
 * Used by {@link ResultCheckerFacade} and {@link ResultAnnouncerFacade} to access winning numbers for a specific draw date.
 */
public interface WinningNumbersProvider {
    Set<Integer> getWinningNumbersByDate(LocalDateTime drawDate);
    boolean areWinningNumbersGeneratedByDate();
}
