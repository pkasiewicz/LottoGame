package pl.pkasiewicz.lottogame.numberreceiver.domain;

import java.util.UUID;

public record TicketId(UUID value) {

    public TicketId {
        if (value == null) {
            throw new IllegalArgumentException("TicketId cannot be null");
        }
    }
}
