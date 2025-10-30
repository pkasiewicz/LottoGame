package pl.pkasiewicz.lottogame.numberreceiver.infrastructure;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pkasiewicz.lottogame.numberreceiver.domain.Ticket;
import pl.pkasiewicz.lottogame.numberreceiver.domain.TicketId;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ElementCollection
    @CollectionTable(
            name = "ticket_numbers",
            joinColumns = @JoinColumn(name = "ticket_id")
    )
    @Column(name = "number", nullable = false)
    private Set<Integer> numbers;

    @Column(nullable = false)
    private LocalDateTime date;

    public Ticket toDomain() {
        return new Ticket(new TicketId(id), numbers, date);
    }

    public static TicketEntity fromDomain(Ticket ticket) {
        return new TicketEntity(ticket.getId().value(), ticket.getNumbers(), ticket.getDrawDate());
    }
}
