package pl.pkasiewicz.lottogame.domain;

import java.time.LocalDateTime;

public interface DrawDateGenerable {

    LocalDateTime getNextDrawDate();
}
