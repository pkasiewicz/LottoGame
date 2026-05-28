package pl.pkasiewicz.lottogame.resultannouncer.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultResponse;
import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultResponseId;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "result_responses")
public class ResultResponseEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID ticketId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Set<Integer> userNumbers;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Set<Integer> wonNumbers;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Set<Integer> hitNumbers;

    @Column(nullable = false)
    private int hitCount;

    @Column(nullable = false)
    private LocalDateTime drawDate;

    @Column(nullable = false)
    private boolean isWinner;

    public ResultResponse toDomain() {
        return new ResultResponse(
                new ResultResponseId(id),
                ticketId,
                userNumbers,
                wonNumbers,
                hitNumbers,
                hitCount,
                drawDate,
                isWinner
        );
    }

    public static ResultResponseEntity fromDomain(ResultResponse resultResponse) {
        return new ResultResponseEntity(
                resultResponse.getId().value(),
                resultResponse.getTicketId(),
                resultResponse.getUserNumbers(),
                resultResponse.getWonNumbers(),
                resultResponse.getHitNumbers(),
                resultResponse.getHitCount(),
                resultResponse.getDrawDate(),
                resultResponse.isWinner()
        );
    }
}
