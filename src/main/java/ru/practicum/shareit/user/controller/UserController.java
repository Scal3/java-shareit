package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getOneUserById(@PathVariable @Positive long id) {
        return userService.getOneUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid CreateUserDto dto) {
        return userService.createUser(dto);
    }

    @PatchMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(
            @PathVariable @Positive long id,
            @RequestBody UpdateUserDto dto
    ) {
        dto.setId(id);

        return userService.updateUser(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @Positive long id) {
        userService.deleteUser(id);
    }
}
