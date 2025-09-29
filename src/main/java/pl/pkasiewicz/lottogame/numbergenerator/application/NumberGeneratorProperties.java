package pl.pkasiewicz.lottogame.numbergenerator.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lotto.number-generator.service")
public record NumberGeneratorProperties(
        int count,
        int lowerBand,
        int upperBand) {
}
