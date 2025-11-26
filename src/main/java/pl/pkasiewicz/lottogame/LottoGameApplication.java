package pl.pkasiewicz.lottogame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.pkasiewicz.lottogame.numbergenerator.application.WinningNumbersGeneratorProperties;
import pl.pkasiewicz.lottogame.numberreceiver.application.NumberReceiverProperties;

@SpringBootApplication
@EnableConfigurationProperties({WinningNumbersGeneratorProperties.class, NumberReceiverProperties.class})
public class LottoGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(LottoGameApplication.class, args);
    }

}
