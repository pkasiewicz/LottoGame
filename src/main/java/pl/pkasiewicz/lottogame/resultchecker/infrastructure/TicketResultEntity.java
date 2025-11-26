package pl.pkasiewicz.lottogame.resultchecker.infrastructure;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResult;
import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResultId;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID ticketId;

    @ElementCollection
    @CollectionTable(
            name = "ticket_result_user_numbers",
            joinColumns = @JoinColumn(name = "ticket_result_id")
    )
    @Column(name = "user_numbers", nullable = false)
    private Set<Integer> userNumbers;

    @ElementCollection
    @CollectionTable(
            name = "ticket_result_user_hit_numbers",
            joinColumns = @JoinColumn(name = "ticket_result_id")
    )
    @Column(name = "hit_numbers", nullable = false)
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
