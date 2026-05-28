package pl.pkasiewicz.lottogame;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.pkasiewicz.lottogame.numbergenerator.application.WinningNumbersGeneratorProperties;
import pl.pkasiewicz.lottogame.numberreceiver.application.NumberReceiverProperties;

@SpringBootApplication
@EnableConfigurationProperties({WinningNumbersGeneratorProperties.class, NumberReceiverProperties.class})
@EnableScheduling
@OpenAPIDefinition(
        info = @Info(
                title = "Lotto Game API",
                version = "1.0",
                description = "API for lottery number submission and result checking"
        )
)
public class LottoGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(LottoGameApplication.class, args);
    }

}
