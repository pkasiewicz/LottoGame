package pl.pkasiewicz.lottogame.numbergenerator.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class WinningNumbers {

    private final WinningNumbersId id;
    private final Set<Integer> winningNumbers;
    private final LocalDateTime date;
}
