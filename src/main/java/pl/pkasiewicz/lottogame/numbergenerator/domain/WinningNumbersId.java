package pl.pkasiewicz.lottogame.numbergenerator.domain;

import java.util.UUID;

public record WinningNumbersId(UUID value) {

    public WinningNumbersId {
        if (value == null) {
            throw new IllegalArgumentException("WinningNumbersId value cannot be null");
        }
    }
}
