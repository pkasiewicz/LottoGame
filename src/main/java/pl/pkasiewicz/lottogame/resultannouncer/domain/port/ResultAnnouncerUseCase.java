package pl.pkasiewicz.lottogame.resultannouncer.domain.port;

import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultAnnouncement;

import java.util.UUID;

public interface ResultAnnouncerUseCase {

    ResultAnnouncement checkResult(UUID id);
}
