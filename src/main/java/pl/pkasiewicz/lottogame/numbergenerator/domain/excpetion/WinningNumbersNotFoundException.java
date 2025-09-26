package pl.pkasiewicz.lottogame.numbergenerator.domain.excpetion;

public class WinningNumbersNotFoundException extends RuntimeException {
    public WinningNumbersNotFoundException(String message) {
        super(message);
    }
}
