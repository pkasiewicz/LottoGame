package pl.pkasiewicz.lottogame.numbergenerator.domain.exception;

public class NumberOutOfRangeException extends RuntimeException {
    public NumberOutOfRangeException(String message) {
        super(message);
    }
}
