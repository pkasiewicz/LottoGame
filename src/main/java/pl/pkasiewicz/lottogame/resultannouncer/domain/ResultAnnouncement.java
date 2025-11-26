package pl.pkasiewicz.lottogame.resultannouncer.domain;

import lombok.Builder;

@Builder
public record ResultAnnouncement(ResultStatus status, ResultResponse result) {
}

