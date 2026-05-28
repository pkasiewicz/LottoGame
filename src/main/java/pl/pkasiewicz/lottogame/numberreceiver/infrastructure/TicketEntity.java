package pl.pkasiewicz.lottogame.numberreceiver.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pl.pkasiewicz.lottogame.numberreceiver.domain.Ticket;
import pl.pkasiewicz.lottogame.numberreceiver.domain.TicketId;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TicketEntity {

    @Id
    private UUID id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Set<Integer> numbers;

    @Column(nullable = false)
    private LocalDateTime drawDate;

    public Ticket toDomain() {
        return new Ticket(new TicketId(id), numbers, drawDate);
    }

    public static TicketEntity fromDomain(Ticket ticket) {
        return new TicketEntity(ticket.getId().value(), ticket.getNumbers(), ticket.getDrawDate());
    }
}
