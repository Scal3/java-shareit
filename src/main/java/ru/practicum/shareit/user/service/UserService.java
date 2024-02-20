package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptionimp.ConflictException;
import ru.practicum.shareit.exception.exceptionimp.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private long id;

    private final Map<Long, User> inMemoryUsers = new HashMap<>();

    private final UserMapper mapper;

    public List<UserDto> getAllUsers() {
        return inMemoryUsers.values().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getOneUserById(long id) {
        User user = inMemoryUsers.get(id);

        if (user == null) throw new NotFoundException("User with id " + id + " is not found");

        return mapper.toDto(user);
    }

    public UserDto createUser(CreateUserDto createUserDto) {
        if (isDuplicateUserEmail(createUserDto.getEmail()))
            throw new ConflictException("User with email " + createUserDto.getEmail() + " already exists");

        UserDto userDto = new UserDto();
        userDto.setId(++id);
        userDto.setName(createUserDto.getName());
        userDto.setEmail(createUserDto.getEmail());

        inMemoryUsers.put(id, mapper.fromDto(userDto));

        return userDto;
    }

    public UserDto updateUser(UpdateUserDto dto) {
        User userForUpdate = inMemoryUsers.get(dto.getId());

        if (userForUpdate == null)
            throw new NotFoundException("User with id " + dto.getId() + " is not found");

        if (!userForUpdate.getEmail().equals(dto.getEmail()) && isDuplicateUserEmail(dto.getEmail()))
            throw new ConflictException("User with email " + dto.getEmail() + " already exists");

        String newName = dto.getName() != null
                ? dto.getName()
                : userForUpdate.getName();

        String newEmail = dto.getEmail() != null
                ? dto.getEmail()
                : userForUpdate.getEmail();

        userForUpdate.setName(newName);
        userForUpdate.setEmail(newEmail);
        inMemoryUsers.replace(dto.getId(), userForUpdate);

        return mapper.toDto(userForUpdate);
    }

    public void deleteUser(long id) {
        if (!inMemoryUsers.containsKey(id))
            throw new NotFoundException("User with id " + id + " is not found");

        inMemoryUsers.remove(id);
    }

    private boolean isDuplicateUserEmail(String email) {
        Optional<User> duplicateEmailUser = inMemoryUsers.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();

        return duplicateEmailUser.isPresent();
    }
}
