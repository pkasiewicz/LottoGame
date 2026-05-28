package pl.pkasiewicz.lottogame.resultannouncer.domain.port;

import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultAnnouncement;

import java.util.UUID;

/**
 * Use case interface for announcing lottery results.
 * It defines the contract for checking the result of a lottery ticket based on its unique identifier (UUID).
 */
public interface ResultAnnouncerUseCase {
    /**
     * Checks the result of a lottery ticket using its unique identifier (UUID).
     *
     * @param id the UUID of the ticket to check
     * @return the result announcement for the ticket
     */
    ResultAnnouncement checkResult(UUID id);
}
