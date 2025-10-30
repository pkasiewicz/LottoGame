package pl.pkasiewicz.lottogame.numberreceiver.domain.exception;

public class InvalidTicketNumbersException extends RuntimeException {
    public InvalidTicketNumbersException(String message) {
        super(message);
    }
}
