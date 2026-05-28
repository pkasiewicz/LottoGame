package pl.pkasiewicz.lottogame.numbergenerator.domain.port;

import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbers;
import pl.pkasiewicz.lottogame.numbergenerator.domain.exception.WinningNumbersNotFoundException;

import java.time.LocalDateTime;

/**
 * Use case interface for generating and retrieving winning numbers in the lottery game.
 */
public interface WinningNumbersGeneratorUseCase {
    /**
     * Generates winning numbers for the next draw date.
     *
     * @return the generated WinningNumbers object
     */
    WinningNumbers generateWinningNumbers();

    /**
     * Retrieves winning numbers for a specific date.
     *
     * @param date the date for which to retrieve the winning numbers
     * @return the retrieved WinningNumbers object
     * @throws WinningNumbersNotFoundException if no winning numbers are found for the specified date
     */
    WinningNumbers retrieveWinningNumbersByDate(LocalDateTime date);

    /**
     * Checks if winning numbers have already been generated for the next draw date.
     *
     * @return true if winning numbers have already been generated for the next draw date, false otherwise
     */
    boolean areWinningNumbersGeneratedByDate();
}
