package pl.pkasiewicz.lottogame.numbergenerator.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    private UUID id;

    @Column(nullable = false)
    private Set<Integer> winningNumbers;

    @Column(nullable = false)
    private LocalDateTime date;

    public WinningNumbers toDomain() {
        return new WinningNumbers(new WinningNumbersId(id), winningNumbers, date);
    }

    public static WinningNumbersEntity fromDomain(WinningNumbers winningNumbers) {
        return new WinningNumbersEntity(winningNumbers.getId().value(), winningNumbers.getWinningNumbers(), winningNumbers.getDate());
    }
}
