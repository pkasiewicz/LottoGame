package pl.pkasiewicz.lottogame.numbergenerator.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WinningNumbersJpaRepository extends JpaRepository<WinningNumbersEntity, UUID> {

    Optional<WinningNumbersEntity> findByDrawDate(LocalDateTime date);
    boolean existsByDrawDate(LocalDateTime date);
}
