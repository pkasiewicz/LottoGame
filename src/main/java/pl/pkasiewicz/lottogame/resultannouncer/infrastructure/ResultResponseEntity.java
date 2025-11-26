package pl.pkasiewicz.lottogame.resultannouncer.infrastructure;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultResponse;
import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultResponseId;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResultResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID ticketId;

    @ElementCollection
    @CollectionTable(
            name = "result_response_user_numbers",
            joinColumns = @JoinColumn(name = "result_response_id")
    )
    @Column(name = "user_numbers", nullable = false)
    private Set<Integer> userNumbers;

    @ElementCollection
    @CollectionTable(
            name = "result_response_won_numbers",
            joinColumns = @JoinColumn(name = "result_response_id")
    )
    @Column(name = "won_numbers", nullable = false)
    private Set<Integer> wonNumbers;

    @ElementCollection
    @CollectionTable(
            name = "result_response_user_hit_numbers",
            joinColumns = @JoinColumn(name = "result_response_id")
    )
    @Column(name = "hit_numbers", nullable = false)
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
