package ru.practicum.ewm.service;

import ru.practicum.ewm.controller.admin.UserGetAllParams;
import ru.practicum.ewm.dto.UserCreateDto;
import ru.practicum.ewm.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto add(UserCreateDto userCreateDto);

    List<UserDto> getAll(UserGetAllParams userGetAllParams);

    void delete(long userId);

}
