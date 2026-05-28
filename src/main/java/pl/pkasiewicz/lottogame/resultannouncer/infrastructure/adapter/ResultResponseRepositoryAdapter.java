package pl.pkasiewicz.lottogame.resultannouncer.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultResponse;
import pl.pkasiewicz.lottogame.resultannouncer.domain.port.ResultResponseRepository;
import pl.pkasiewicz.lottogame.resultannouncer.infrastructure.ResultResponseEntity;
import pl.pkasiewicz.lottogame.resultannouncer.infrastructure.ResultResponseJpaRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class ResultResponseRepositoryAdapter implements ResultResponseRepository {

    private final ResultResponseJpaRepository repository;

    @Override
    public ResultResponse save(ResultResponse resultResponse) {
        ResultResponseEntity saved = repository.save(ResultResponseEntity.fromDomain(resultResponse));
        return saved.toDomain();
    }

    @Override
    public Optional<ResultResponse> findByTicketId(UUID ticketId) {
        return repository.findByTicketId(ticketId)
                .map(ResultResponseEntity::toDomain);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
