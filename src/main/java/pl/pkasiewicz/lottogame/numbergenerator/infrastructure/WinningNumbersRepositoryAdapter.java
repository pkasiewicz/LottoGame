package pl.pkasiewicz.lottogame.numbergenerator.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbers;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbersRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WinningNumbersRepositoryAdapter implements WinningNumbersRepository {

    private final WinningNumbersJpaRepository repository;

    @Override
    public WinningNumbers save(WinningNumbers winningNumbers) {
        WinningNumbersEntity saved = repository.save(WinningNumbersEntity.fromDomain(winningNumbers));
        return saved.toDomain();
    }

    @Override
    public Optional<WinningNumbers> findByDate(LocalDateTime date) {
        return repository.findByDate(date)
                .map(WinningNumbersEntity::toDomain);
    }

    @Override
    public boolean existsByDate(LocalDateTime date) {
        return repository.existsByDate(date);
    }
}
