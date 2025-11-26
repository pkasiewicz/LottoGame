package pl.pkasiewicz.lottogame.resultannouncer.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultResponse;
import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultResponseRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResultResponseRepositoryAdapter implements ResultResponseRepository {

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
}
