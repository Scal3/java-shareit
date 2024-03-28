package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.exceptionimp.ConflictException;
import ru.practicum.shareit.exception.exceptionimp.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import org.hibernate.exception.ConstraintViolationException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.debug("Entering getAllUsers method");

        List<User> users = userRepository.findAll();
        List<UserDto> resultDtos =
                modelMapper.map(users, new TypeToken<List<UserDto>>() {}.getType());
        log.info("Mapping from List<User> to List<UserDto>: {}", resultDtos);
        log.debug("Exiting getAllUsers method");

        return resultDtos;
    }

    @Transactional(readOnly = true)
    public UserDto getOneUserById(long id) {
        log.debug("Entering getOneUserById method: id = {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " is not found"));

        UserDto userDto = modelMapper.map(user, UserDto.class);
        log.info("Mapping from User to UserDto: {}", userDto);
        log.debug("Exiting getOneUserById method");

        return userDto;
    }

    @Transactional
    public UserDto createUser(CreateUserDto createUserDto) {
        try {
            log.debug("Entering createUser method: CreateUserDto = {}", createUserDto);

            User userEntity = modelMapper.map(createUserDto, User.class);
            User savedUser = userRepository.save(userEntity);
            UserDto userDtoResult = modelMapper.map(savedUser, UserDto.class);
            log.info("Mapping from User entity to UserDto {}", userDtoResult);
            log.debug("Exiting createUser method");

            return userDtoResult;
        } catch (DataIntegrityViolationException | ConstraintViolationException exc) {
            log.warn("Error has occurred {}", exc.getMessage());

            throw new ConflictException(exc.getMessage());
        }
    }

    @Transactional
    public UserDto updateUser(UpdateUserDto dto) {
        log.debug("Entering updateUser method: UpdateUserDto = {}", dto);

        User userEntity = userRepository.findById(dto.getId())
                .orElseThrow(() ->
                            new NotFoundException("User with id " + dto.getId() + " is not found"));

        String newName = dto.getName() != null
                ? dto.getName()
                : userEntity.getName();

        String newEmail = dto.getEmail() != null
                ? dto.getEmail()
                : userEntity.getEmail();

        userEntity.setName(newName);
        userEntity.setEmail(newEmail);

        try {
            User updatedUser = userRepository.save(userEntity);
            UserDto userDtoResult = modelMapper.map(updatedUser, UserDto.class);
            log.info("Mapping from User entity to UserDto {}", userDtoResult);
            log.debug("Exiting updateUser method");

            return userDtoResult;
        } catch (DataIntegrityViolationException | ConstraintViolationException exc) {
            log.warn("Error has occurred {}", exc.getMessage());

            throw new ConflictException(exc.getMessage());
        }
    }

    @Transactional
    public void deleteUser(long id) {
        log.debug("Entering deleteUser method: id = {}", id);

        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " is not found"));

        userRepository.deleteById(id);
        log.debug("Exiting deleteUser method");
    }
}
