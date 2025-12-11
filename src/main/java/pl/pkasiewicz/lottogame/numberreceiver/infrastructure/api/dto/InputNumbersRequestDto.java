package pl.pkasiewicz.lottogame.numberreceiver.infrastructure.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record InputNumbersRequestDto(
        @NotNull(message = "Numbers cannot be null")
        Set<Integer> numbers
) {
}
