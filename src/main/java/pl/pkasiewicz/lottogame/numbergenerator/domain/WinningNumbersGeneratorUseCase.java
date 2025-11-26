package pl.pkasiewicz.lottogame.numbergenerator.domain;

import java.time.LocalDateTime;

public interface WinningNumbersGeneratorUseCase {

    WinningNumbers generateWinningNumbers();
    WinningNumbers retrieveWinningNumbersByDate(LocalDateTime date);
    boolean areWinningNumbersGeneratedByDate();
}
