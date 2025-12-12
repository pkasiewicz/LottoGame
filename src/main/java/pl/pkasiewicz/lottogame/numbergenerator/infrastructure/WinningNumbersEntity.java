package pl.pkasiewicz.lottogame.numbergenerator.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbers;
import pl.pkasiewicz.lottogame.numbergenerator.domain.WinningNumbersId;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WinningNumbersEntity {

    @Id
    private UUID id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
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
