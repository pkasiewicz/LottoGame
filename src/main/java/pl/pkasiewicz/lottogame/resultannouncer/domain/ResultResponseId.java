package pl.pkasiewicz.lottogame.resultannouncer.domain;

import java.util.UUID;

public record ResultResponseId(UUID value) {

    public ResultResponseId {
        if (value == null) {
            throw new IllegalArgumentException("ResultResponseId cannot be null");
        }
    }
}
