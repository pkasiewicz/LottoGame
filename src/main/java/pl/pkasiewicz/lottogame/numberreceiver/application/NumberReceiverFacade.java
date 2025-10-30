package pl.pkasiewicz.lottogame.numberreceiver.application;

import org.springframework.stereotype.Service;
import pl.pkasiewicz.lottogame.domain.IdGenerable;
import pl.pkasiewicz.lottogame.numberreceiver.domain.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class NumberReceiverFacade implements NumberReceiverUseCase {

    private final NumberReceiverValidator validator;
    private final DrawDateGenerator drawDateGenerator;
    private final TicketRepository repository;
    private final IdGenerable idGenerator;

    public NumberReceiverFacade(NumberReceiverProperties properties,
                                TicketRepository repository,
                                IdGenerable idGenerator,
                                Clock clock) {
        this.validator = new NumberReceiverValidator(properties);
        this.drawDateGenerator = new DrawDateGenerator(clock);
        this.repository = repository;
        this.idGenerator = idGenerator;
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
