package pl.pkasiewicz.lottogame.resultannouncer.infrastructure.api.dto;

import pl.pkasiewicz.lottogame.resultannouncer.domain.ResultAnnouncement;

public record ResultAnnouncementDto(
        String status,
        ResultDto result
) {
    public static ResultAnnouncementDto from(ResultAnnouncement announcement) {
        return new ResultAnnouncementDto(
            announcement.status().name(),
                announcement.result() != null ? ResultDto.from(announcement.result()) : null
        );
    }
}
