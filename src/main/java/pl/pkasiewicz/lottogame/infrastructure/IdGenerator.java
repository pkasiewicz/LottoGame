package pl.pkasiewicz.lottogame.infrastructure;

import org.springframework.stereotype.Component;
import pl.pkasiewicz.lottogame.domain.IdGenerable;

import java.util.UUID;

@Component
public class IdGenerator implements IdGenerable {

    @Override
    public UUID generateId() {
        return UUID.randomUUID();
    }
}
