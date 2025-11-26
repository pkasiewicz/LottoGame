package pl.pkasiewicz.lottogame.resultchecker.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResult;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResultRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TicketResultRepositoryAdapter implements TicketResultRepository {

    private final TicketResultJpaRepository  repository;

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
}
