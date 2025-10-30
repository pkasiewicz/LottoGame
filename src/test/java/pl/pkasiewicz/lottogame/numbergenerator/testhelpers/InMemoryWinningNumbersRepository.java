package pl.pkasiewicz.lottogame.numbergenerator.testhelpers;

import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbers;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbersRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryWinningNumbersRepository implements WinningNumbersRepository {

    private final Map<UUID, WinningNumbers> db = new ConcurrentHashMap<>();

    @Override
    public WinningNumbers save(WinningNumbers winningNumbers) {
        db.put(winningNumbers.getId().value(), winningNumbers);
        return winningNumbers;
    }

    @Override
    public Optional<WinningNumbers> findByDate(LocalDateTime date) {
        return db.values()
                .stream()
                .filter(ticket -> ticket.getDate().isEqual(date))
                .findFirst();
    }

    @Override
    public boolean existsByDate(LocalDateTime date) {
        return findByDate(date).isPresent();
    }
}
