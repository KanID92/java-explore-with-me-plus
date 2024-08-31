package ru.practicum.ewm.controller.admin;

public record UserGetAllParams(
        Long[] ids,
        int from,
        int size
) {
}
