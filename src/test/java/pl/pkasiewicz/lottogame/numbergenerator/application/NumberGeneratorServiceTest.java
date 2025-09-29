package pl.pkasiewicz.lottogame.numbergenerator.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbers;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbersId;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbersRepository;
import pl.pkasiewicz.lottogame.numbergenerator.domain.excpetion.WinningNumbersNotFoundException;
import pl.pkasiewicz.lottogame.numbergenerator.infrastructure.LocalRandomNumberGenerator;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NumberGeneratorServiceTest {

    @Mock
    private WinningNumbersRepository repository;

    @Mock
    private LocalRandomNumberGenerator generator;

    @Mock
    private NumberGeneratorProperties properties;

    @InjectMocks
    private NumberGeneratorService service;

    @BeforeEach
    void setUp() {
        service = new NumberGeneratorService(generator, properties, repository);
    }

    @Test
    public void should_generate_valid_random_numbers() {
        // given
        Set<Integer> generatedNumbers = Set.of(1, 2, 3, 4, 5, 6);
        when(repository.existsByDate(any(LocalDateTime.class))).thenReturn(false);
        when(generator.generateRandomNumbers(6, 1, 49)).thenReturn(generatedNumbers);
        when(properties.count()).thenReturn(6);
        when(properties.lowerBand()).thenReturn(1);
        when(properties.upperBand()).thenReturn(49);
        when(repository.save(any(WinningNumbers.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        WinningNumbers result = service.generateWinningNumbers();

        // then
        assertThat(result.getWinningNumbers()).isEqualTo(generatedNumbers);
        assertThat(result.getWinningNumbers()).hasSize(6);
        verify(repository).save(any(WinningNumbers.class));
        verify(generator).generateRandomNumbers(6, 1, 49);
    }

    @Test
    public void should_return_existing_numbers_when_date_already_exists() {
        // given
        LocalDateTime date = LocalDateTime.now();
        WinningNumbers expected = new WinningNumbers(
                new WinningNumbersId(UUID.randomUUID()),
                Set.of(1, 2, 3, 4, 5, 6),
                date
        );
        when(repository.existsByDate(any(LocalDateTime.class))).thenReturn(true);
        when(repository.findByDate(any(LocalDateTime.class))).thenReturn(Optional.of(expected));

        // when
        WinningNumbers result = service.generateWinningNumbers();

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void should_retrieve_winning_numbers_by_date() {
        // given
        LocalDateTime date = LocalDateTime.now();
        WinningNumbers expected = new WinningNumbers(
                new WinningNumbersId(UUID.randomUUID()),
                Set.of(1, 2, 3, 4, 5, 6),
                date
        );
        when(repository.findByDate(any(LocalDateTime.class))).thenReturn(Optional.of(expected));

        // when
        WinningNumbers actual = service.retrieveWinningNumbersByDate(date);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void should_throw_exception_when_numbers_not_found_for_date() {
        // given
        LocalDateTime date = LocalDateTime.now();
        when(repository.findByDate(date)).thenReturn(Optional.empty());

        // when && then
        assertThatThrownBy(() -> service.retrieveWinningNumbersByDate(date))
                .isInstanceOf(WinningNumbersNotFoundException.class)
                .hasMessage("Winning numbers not found");
    }
}