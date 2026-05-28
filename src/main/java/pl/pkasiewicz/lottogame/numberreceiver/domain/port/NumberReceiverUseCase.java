package pl.pkasiewicz.lottogame.numberreceiver.domain.port;

import pl.pkasiewicz.lottogame.numberreceiver.domain.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Interface representing the use case for receiving lottery numbers and managing tickets.
 */
public interface NumberReceiverUseCase {
    /**
     * Inputs the lottery numbers chosen by the user.
     *
     * @param numbers A set of integers representing the lottery numbers chosen by the user.
     * @return the created Ticket object
     */
    Ticket inputNumbers(Set<Integer> numbers);

    /**
     * Retrieves all tickets submitted for the next lottery draw.
     *
     * @param nextDrawDate The date and time of the next lottery draw
     * @return A List of tickets submitted for next lottery draw
     */
    List<Ticket> retrieveAllTicketsByNextDrawDate(LocalDateTime nextDrawDate);

    /**
     * Checks if a ticket with the given ID exists.
     *
     * @param ticketId the UUID of the ticket to check
     * @return true if ticket exists, false otherwise
     */
    boolean ticketExists(UUID ticketId);
}
