package pl.pkasiewicz.lottogame.numberreceiver.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pkasiewicz.lottogame.numberreceiver.domain.Ticket;
import pl.pkasiewicz.lottogame.numberreceiver.domain.port.TicketRepository;
import pl.pkasiewicz.lottogame.numberreceiver.infrastructure.TicketEntity;
import pl.pkasiewicz.lottogame.numberreceiver.infrastructure.TicketJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class TicketRepositoryAdapter implements TicketRepository {

    private final TicketJpaRepository repository;

    @Override
    public Ticket save(Ticket ticket) {
        TicketEntity saved = repository.save(TicketEntity.fromDomain(ticket));
        return saved.toDomain();
    }

    @Override
    public List<Ticket> findAllTicketsByDrawDate(LocalDateTime drawDate) {
        return repository.findAllTicketsByDrawDate(drawDate).stream()
                .map(TicketEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Ticket> findById(UUID id) {
        return repository.findById(id)
                .map(TicketEntity::toDomain);
    }

    @Override
    public List<Ticket> findAll() {
        return repository.findAll().stream()
                .map(TicketEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
