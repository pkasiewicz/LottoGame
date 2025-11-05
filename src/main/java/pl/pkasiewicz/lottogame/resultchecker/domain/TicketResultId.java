package pl.pkasiewicz.lottogame.resultchecker.domain;

import java.util.UUID;

public record TicketResultId(UUID value) {

    public TicketResultId {
        if (value == null) {
            throw new IllegalArgumentException("TicketResultId cannot be null");
        }
    }
}
