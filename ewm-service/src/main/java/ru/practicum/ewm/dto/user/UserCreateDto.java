package ru.practicum.ewm.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UserCreateDto(
        @NotNull @Email String email,
        @NotNull @Min(3) @Max(64) String name
) {
}
