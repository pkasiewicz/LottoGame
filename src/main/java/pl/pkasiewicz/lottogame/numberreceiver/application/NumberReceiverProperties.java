package pl.pkasiewicz.lottogame.numberreceiver.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lotto.number-receiver")
public record NumberReceiverProperties(
        int count,
        int lowerBand,
        int upperBand) {
}
