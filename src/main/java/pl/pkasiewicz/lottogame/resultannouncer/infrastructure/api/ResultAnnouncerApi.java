package pl.pkasiewicz.lottogame.resultannouncer.infrastructure.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.pkasiewicz.lottogame.resultannouncer.infrastructure.api.dto.ResultAnnouncementDto;

import java.util.UUID;

@Tag(name = "Result Announcer", description = "Endpoint for checking lottery ticket results")
@RequestMapping("/api/results")
public interface ResultAnnouncerApi {

    @GetMapping("/{ticketId}")
    @Operation(
            summary = "Check lottery ticket result",
            description = "Returns the result of a lottery ticket. Possible statuses: " +
                    "ALREADY_CHECKED (result was checked before), " +
                    "WIN_MESSAGE/LOSE_MESSAGE (draw completed), " +
                    "WAITING_FOR_DRAW (draw not yet completed - check back on Saturday at 12:00), " +
                    "TICKET_NOT_FOUND (invalid ticket ID)"
    )
    @ApiResponse(responseCode = "200", description = "Result successfully retrieved")
    ResponseEntity<ResultAnnouncementDto> checkResult(@PathVariable UUID ticketId);
}
