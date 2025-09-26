package pl.pkasiewicz.lottogame.numbergenerator.domain;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WinningNumbersRepository {

    void save(WinningNumbers winningNumbers);
    Optional<WinningNumbers> findByDate(LocalDateTime date);
    boolean existsByDate(LocalDateTime date);
}