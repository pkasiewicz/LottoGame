package pl.pkasiewicz.lottogame.numberreceiver.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TicketJpaRepository extends JpaRepository<TicketEntity, UUID> {

    List<TicketEntity> findAllTicketsByDrawDate(LocalDateTime drawDate);
}
