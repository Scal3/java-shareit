package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.exceptionimp.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {

    private static final long INITIAL_USER_ID = 1;

    private static final String INITIAL_USER_NAME = "user1";

    private static final String INITIAL_USER_EMAIL = "user1@mail.ru";

    private final UserRepository userRepository;

    private final UserService userService;

    @BeforeEach
    void setup() {
        createInitialUser(INITIAL_USER_ID, INITIAL_USER_NAME, INITIAL_USER_EMAIL);
    }

    private User createInitialUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return userRepository.save(user);
    }

    @Test
    void getAllUsers() {
        List<UserDto> users = userService.getAllUsers();

        assertEquals(INITIAL_USER_ID, users.get(0).getId());
        assertEquals(INITIAL_USER_NAME, users.get(0).getName());
        assertEquals(INITIAL_USER_EMAIL, users.get(0).getEmail());
    }

    @Test
    void getOneUserById() {
        UserDto user = userService.getOneUserById(INITIAL_USER_ID);

        assertEquals(INITIAL_USER_ID, user.getId());
        assertEquals(INITIAL_USER_NAME, user.getName());
        assertEquals(INITIAL_USER_EMAIL, user.getEmail());
    }

    @Test
    void createUser() {
        CreateUserDto dto = new CreateUserDto();
        dto.setName("new user");
        dto.setEmail("newUserEmail@mail.ru");

        UserDto user = userService.createUser(dto);

        assertEquals(2, user.getId());
        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());
    }

    @Test
    void updateUser() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(INITIAL_USER_ID);
        dto.setName("updated user");
        dto.setEmail("updatedUserEmail@mail.ru");

        UserDto user = userService.updateUser(dto);

        assertEquals(INITIAL_USER_ID, user.getId());
        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());
    }

    @Test
    void deleteUser() {
        userService.deleteUser(INITIAL_USER_ID);

        assertThrows(NotFoundException.class, () -> userService.getOneUserById(INITIAL_USER_ID));
    }
}