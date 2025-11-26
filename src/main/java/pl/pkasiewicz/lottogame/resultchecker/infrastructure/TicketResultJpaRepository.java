package pl.pkasiewicz.lottogame.resultchecker.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketResultJpaRepository extends JpaRepository<TicketResultEntity, UUID> {

    Optional<TicketResultEntity> findByTicketId(UUID ticketId);
}
