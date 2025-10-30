package pl.pkasiewicz.lottogame.numbergenerator.domain.exception;

public class WinningNumbersNotFoundException extends RuntimeException {
    public WinningNumbersNotFoundException(String message) {
        super(message);
    }
}
