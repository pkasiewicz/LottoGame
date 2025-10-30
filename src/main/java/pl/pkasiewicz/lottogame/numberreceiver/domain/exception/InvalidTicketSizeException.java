package pl.pkasiewicz.lottogame.numberreceiver.domain.exception;

public class InvalidTicketSizeException extends RuntimeException {
    public InvalidTicketSizeException(String message) {
        super(message);
    }
}
