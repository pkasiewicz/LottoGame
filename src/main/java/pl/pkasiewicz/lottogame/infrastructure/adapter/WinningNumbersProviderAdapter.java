package pl.pkasiewicz.lottogame.infrastructure.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.pkasiewicz.lottogame.numbergenerator.domain.port.WinningNumbersGeneratorUseCase;
import pl.pkasiewicz.lottogame.domain.port.WinningNumbersProvider;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@AllArgsConstructor
class WinningNumbersProviderAdapter implements WinningNumbersProvider {

    private final WinningNumbersGeneratorUseCase winningNumbersGeneratorFacade;

    @Override
    public Set<Integer> getWinningNumbersByDate(LocalDateTime drawDate) {
        return winningNumbersGeneratorFacade.retrieveWinningNumbersByDate(drawDate).getWinningNumbers();
    }

    @Override
    public boolean areWinningNumbersGeneratedByDate() {
        return winningNumbersGeneratorFacade.areWinningNumbersGeneratedByDate();
    }
}

