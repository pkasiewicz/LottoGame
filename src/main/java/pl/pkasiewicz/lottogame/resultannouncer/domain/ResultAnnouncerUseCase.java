package pl.pkasiewicz.lottogame.resultannouncer.domain;

import java.util.UUID;

public interface ResultAnnouncerUseCase {

    ResultAnnouncement checkResult(UUID id);
}
