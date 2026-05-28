package pl.pkasiewicz.lottogame.numbergenerator.domain.port;

import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbers;

import java.time.LocalDateTime;

public interface WinningNumbersGeneratorUseCase {

    WinningNumbers generateWinningNumbers();
    WinningNumbers retrieveWinningNumbersByDate(LocalDateTime date);
    boolean areWinningNumbersGeneratedByDate();
}
