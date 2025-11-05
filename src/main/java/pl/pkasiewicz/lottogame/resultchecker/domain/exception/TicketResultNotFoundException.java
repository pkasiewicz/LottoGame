package pl.pkasiewicz.lottogame.resultchecker.domain.exception;

public class TicketResultNotFoundException extends RuntimeException {
    public TicketResultNotFoundException(String message) {
        super(message);
    }
}
