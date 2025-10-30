package pl.pkasiewicz.lottogame.numbergenerator.domain.exception;

public class InvalidNumberCountException extends RuntimeException {
    public InvalidNumberCountException(String message) {
        super(message);
    }
}
