package pl.pkasiewicz.lottogame.resultannouncer.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResultResponseJpaRepository extends JpaRepository<ResultResponseEntity, UUID> {
    Optional<ResultResponseEntity> findByTicketId(UUID ticketId);
}
