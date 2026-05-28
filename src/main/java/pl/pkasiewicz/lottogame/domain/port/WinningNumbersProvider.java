package pl.pkasiewicz.lottogame.domain.port;

import java.time.LocalDateTime;
import java.util.Set;

public interface WinningNumbersProvider {
    Set<Integer> getWinningNumbersByDate(LocalDateTime drawDate);
    boolean areWinningNumbersGeneratedByDate();
}
