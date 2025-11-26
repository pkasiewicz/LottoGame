package pl.pkasiewicz.lottogame.numbergenerator.infrastructure;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbers;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbersId;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WinningNumbersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ElementCollection
    @CollectionTable(
            name = "winning_numbers_values",
            joinColumns = @JoinColumn(name = "winning_numbers_id")
    )
    @Column(name = "winning_numbers", nullable = false)
    private Set<Integer> winningNumbers;

    @Column(nullable = false)
    private LocalDateTime drawDate;

    public WinningNumbers toDomain() {
        return new WinningNumbers(new WinningNumbersId(id), winningNumbers, drawDate);
    }

    public static WinningNumbersEntity fromDomain(WinningNumbers winningNumbers) {
        return new WinningNumbersEntity(
                winningNumbers.getId().value(),
                winningNumbers.getWinningNumbers(),
                winningNumbers.getDrawDate()
        );
    }
}
