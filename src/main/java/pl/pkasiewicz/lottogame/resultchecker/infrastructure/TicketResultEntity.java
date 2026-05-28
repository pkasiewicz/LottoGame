package pl.pkasiewicz.lottogame.resultchecker.infrastructure;

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
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResult;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResultId;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "ticket_results")
public class TicketResultEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID ticketId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Set<Integer> userNumbers;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Set<Integer> hitNumbers;

    @Column(nullable = false)
    private int hitCount;

    @Column(nullable = false)
    private LocalDateTime drawDate;

    @Column(nullable = false)
    private boolean isWinner;

    public TicketResult toDomain() {
        return new TicketResult(
                new TicketResultId(id),
                ticketId,
                userNumbers,
                hitNumbers,
                hitCount,
                drawDate,
                isWinner
        );
    }

    public static TicketResultEntity fromDomain(TicketResult ticketResult) {
        return new TicketResultEntity(
                ticketResult.getId().value(),
                ticketResult.getTicketId(),
                ticketResult.getUserNumbers(),
                ticketResult.getHitNumbers(),
                ticketResult.getHitCount(),
                ticketResult.getDrawDate(),
                ticketResult.isWinner()
        );
    }
}
