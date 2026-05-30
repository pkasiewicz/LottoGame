package pl.pkasiewicz.lottogame.domain.port;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Port for retrieving winning numbers based on the draw date.
 * Used by the result checker and result announcer to access winning numbers for a specific draw date.
 */
public interface WinningNumbersProvider {
    Set<Integer> getWinningNumbersByDate(LocalDateTime drawDate);
    boolean areWinningNumbersGeneratedByDate();
}
