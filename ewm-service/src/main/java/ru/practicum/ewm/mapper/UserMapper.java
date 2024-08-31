package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.user.UserCreateDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User userCreateDtoToUser(UserCreateDto userCreateDto);

    UserDto userToUserDto(User user);

    UserShortDto userToUserShotDto(User user);

}
