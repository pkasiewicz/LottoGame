package pl.pkasiewicz.lottogame.numbergenerator.domain;

import java.time.LocalDateTime;

public interface NumberGeneratorUseCase {

    WinningNumbers generateWinningNumbers();
    WinningNumbers retrieveWinningNumbersByDate(LocalDateTime date);
}
