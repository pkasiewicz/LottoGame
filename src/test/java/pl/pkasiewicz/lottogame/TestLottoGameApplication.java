package pl.pkasiewicz.lottogame;

import org.springframework.boot.SpringApplication;

public class TestLottoGameApplication {

    public static void main(String[] args) {
        SpringApplication.from(LottoGameApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
