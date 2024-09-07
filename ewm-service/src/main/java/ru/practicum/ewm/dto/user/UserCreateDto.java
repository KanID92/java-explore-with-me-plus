package ru.practicum.ewm.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
        @NotNull @Email String email,
        @NotNull @Size(min = 3, max = 64) String name
) {
}
