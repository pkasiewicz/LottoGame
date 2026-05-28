package pl.pkasiewicz.lottogame.resultannouncer.infrastructure.api;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultAnnouncement;
import pl.pkasiewicz.lottogame.resultannouncer.domain.port.ResultAnnouncerUseCase;
import pl.pkasiewicz.lottogame.resultannouncer.infrastructure.api.dto.ResultAnnouncementDto;

import java.util.UUID;

import static pl.pkasiewicz.lottogame.resultannouncer.domain.ResultStatus.TICKET_NOT_FOUND;

/**
 * REST controller for handling lottery result announcements.
 * Delegates business logic to {@link ResultAnnouncerUseCase}.
 */
@RestController
@AllArgsConstructor
public class ResultAnnouncerController implements ResultAnnouncerApi {

    private final ResultAnnouncerUseCase resultAnnouncerFacade;

    @Override
    public ResponseEntity<ResultAnnouncementDto> checkResult(@PathVariable UUID ticketId) {
        ResultAnnouncement result = resultAnnouncerFacade.checkResult(ticketId);
        if (result.status() == TICKET_NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResultAnnouncementDto.from(result));
        }
        return ResponseEntity.ok(ResultAnnouncementDto.from(result));
    }
}