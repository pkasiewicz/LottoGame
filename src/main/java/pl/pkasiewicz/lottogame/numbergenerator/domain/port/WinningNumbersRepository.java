package pl.pkasiewicz.lottogame.numbergenerator.domain.port;

import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbers;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WinningNumbersRepository {

    WinningNumbers save(WinningNumbers winningNumbers);
    Optional<WinningNumbers> findByDate(LocalDateTime date);
    boolean existsByDate(LocalDateTime date);
}