package pl.pkasiewicz.lottogame.numbergenerator.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lotto.numbers-generator")
public record WinningNumbersGeneratorProperties(
        int count,
        int lowerBand,
        int upperBand) {
}
