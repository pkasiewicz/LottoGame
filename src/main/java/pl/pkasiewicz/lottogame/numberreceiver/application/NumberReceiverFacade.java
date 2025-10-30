package pl.pkasiewicz.lottogame.numberreceiver.application;

import org.springframework.stereotype.Service;
import pl.pkasiewicz.lottogame.domain.DrawDateGenerable;
import pl.pkasiewicz.lottogame.domain.IdGenerable;
import pl.pkasiewicz.lottogame.numberreceiver.domain.NumberReceiverUseCase;
import pl.pkasiewicz.lottogame.numberreceiver.domain.Ticket;
import pl.pkasiewicz.lottogame.numberreceiver.domain.TicketId;
import pl.pkasiewicz.lottogame.numberreceiver.domain.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class NumberReceiverFacade implements NumberReceiverUseCase {

    private final NumberReceiverValidator validator;
    private final TicketRepository repository;
    private final IdGenerable idGenerator;
    private final DrawDateGenerable drawDateGenerator;

    public NumberReceiverFacade(NumberReceiverProperties properties,
                                TicketRepository repository,
                                IdGenerable idGenerator,
                                DrawDateGenerable drawDateGenerator) {
        this.validator = new NumberReceiverValidator(properties);
        this.repository = repository;
        this.idGenerator = idGenerator;
        this.drawDateGenerator = drawDateGenerator;
    }

    @Override
    public Ticket inputNumbers(Set<Integer> numbers) {
        validator.validate(numbers);
        LocalDateTime drawDate = drawDateGenerator.getNextDrawDate();
        Ticket ticket = new Ticket(
                new TicketId(idGenerator.generateId()),
                numbers,
                drawDate
        );
        return repository.save(ticket);
    }

    @Override
    public List<Ticket> retrieveAllTicketsByNextDrawDate(LocalDateTime nextDrawDate) {
        return repository.findAllTicketsByDrawDate(nextDrawDate);
    }
}
