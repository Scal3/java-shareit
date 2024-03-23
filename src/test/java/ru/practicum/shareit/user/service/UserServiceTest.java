package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.ShareItConfig;
import ru.practicum.shareit.exception.exceptionimp.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    @Test
    void getAllUsers_normal_case_then_return_list_of_UserDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        UserService userService = new UserService(userRepositoryMock, mapper);

        User user = new User();
        user.setId(1);
        user.setName("user");
        user.setEmail("user@email.com");

        Mockito
                .when(userRepositoryMock.findAll())
                .thenReturn(List.of(user));

        List<UserDto> userDtos = userService.getAllUsers();

        assertEquals(user.getId(), userDtos.get(0).getId());
        assertEquals(user.getName(), userDtos.get(0).getName());
        assertEquals(user.getEmail(), userDtos.get(0).getEmail());
    }

    @Test
    void getOneUserById_normal_case_then_return_UserDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        UserService userService = new UserService(userRepositoryMock, mapper);

        User user = new User();
        user.setId(1);
        user.setName("user");
        user.setEmail("user@email.com");

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        UserDto userDto = userService.getOneUserById(1);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void getOneUserById_user_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        UserService userService = new UserService(userRepositoryMock, mapper);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getOneUserById(1000));
    }

    @Test
    void createUser_normal_case_then_return_UserDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        UserService userService = new UserService(userRepositoryMock, mapper);

        CreateUserDto dto = new CreateUserDto();
        dto.setName("user");
        dto.setEmail("user@email.com");

        User user = new User();
        user.setId(1);
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        Mockito
                .when(userRepositoryMock.save(Mockito.any()))
                .thenReturn(user);

        UserDto userDto = userService.createUser(dto);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());

        Mockito.verify(userRepositoryMock, Mockito.times(1))
                .save(any(User.class));
    }

    @Test
    void updateUser_normal_case_then_return_UserDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        UserService userService = new UserService(userRepositoryMock, mapper);

        User user = new User();
        user.setId(1);
        user.setName("user");
        user.setEmail("user@email.com");

        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(1L);
        dto.setName("updated user");
        dto.setEmail("updated@email.com");

        User userUpdated = new User();
        userUpdated.setId(dto.getId());
        userUpdated.setName(dto.getName());
        userUpdated.setEmail(dto.getEmail());

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(userRepositoryMock.save(Mockito.any()))
                .thenReturn(userUpdated);

        UserDto userDto = userService.updateUser(dto);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());

        Mockito.verify(userRepositoryMock, Mockito.times(1))
                .save(any(User.class));
    }

    @Test
    void updateUser_user_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        UserService userService = new UserService(userRepositoryMock, mapper);

        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(1000L);
        dto.setName("updated user");
        dto.setEmail("updated@email.com");

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(dto));

        Mockito.verify(userRepositoryMock, Mockito.times(0))
                .save(any(User.class));
    }

    @Test
    void deleteUser_user_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        UserService userService = new UserService(userRepositoryMock, mapper);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(1000));
    }
}