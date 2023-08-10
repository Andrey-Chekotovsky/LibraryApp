package net.chekotovsky.LibraryApp.mapper;

import net.chekotovsky.LibraryApp.DTO.UserDto;
import net.chekotovsky.LibraryApp.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {
    public UserDto map(User user)
    {
        return new UserDto().toBuilder()
                .id(user.getId())
                .role(user.getRole())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(user.getPassword())
                .enabled(user.isEnabled())
                .build();
    }

    public User map(UserDto userDto)
    {
        return new User().toBuilder()
                .id(userDto.getId())
                .role(userDto.getRole())
                .username(userDto.getUsername())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .password(userDto.getPassword())
                .enabled(userDto.isEnabled())
                .build();
    }
}
