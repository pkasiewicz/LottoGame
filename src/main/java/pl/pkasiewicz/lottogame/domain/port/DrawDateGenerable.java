package pl.pkasiewicz.lottogame.domain.port;

import java.time.LocalDateTime;

public interface DrawDateGenerable {

    LocalDateTime getNextDrawDate();
}
