package pl.pkasiewicz.lottogame.resultchecker.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResult;
import pl.pkasiewicz.lottogame.resultchecker.domain.port.TicketResultRepository;
import pl.pkasiewicz.lottogame.resultchecker.infrastructure.TicketResultEntity;
import pl.pkasiewicz.lottogame.resultchecker.infrastructure.TicketResultJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class TicketResultRepositoryAdapter implements TicketResultRepository {

    private final TicketResultJpaRepository repository;

    @Override
    public TicketResult save(TicketResult ticketResult) {
        TicketResultEntity saved = repository.save(TicketResultEntity.fromDomain(ticketResult));
        return saved.toDomain();
    }

    @Override
    public Optional<TicketResult> findByTicketId(UUID ticketId) {
        return repository.findByTicketId(ticketId)
                .map(TicketResultEntity::toDomain);
    }

    @Override
    public List<TicketResult> findAll() {
        return repository.findAll().stream()
                .map(TicketResultEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
