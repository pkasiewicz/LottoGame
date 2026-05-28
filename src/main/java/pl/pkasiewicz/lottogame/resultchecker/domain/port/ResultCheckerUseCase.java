package pl.pkasiewicz.lottogame.resultchecker.domain.port;

import pl.pkasiewicz.lottogame.resultchecker.domain.TicketResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Use case interface for checking lottery results.
 * It defines methods for generating results and retrieving results for specific tickets.
 */
public interface ResultCheckerUseCase {
    /**
     * Generates results for all lottery tickets.
     * This method is responsible for processing the tickets and determining their results based on the lottery rules.
     *
     * @return a list of all generated ticket results
     */
    List<TicketResult> generateResults();

    /**
     * Retrieves the result for a specific lottery ticket based on its unique identifier (UUID).
     *
     * @param id the unique identifier of the ticket
     * @return an Optional containing the ticket result if found, or empty if not found
     */
    Optional<TicketResult> getResultForTicket(UUID id);
}
