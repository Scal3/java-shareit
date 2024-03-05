package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.exceptionimp.ConflictException;
import ru.practicum.shareit.exception.exceptionimp.InternalServerException;
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
        try {
            log.debug("Entering getAllUsers method");

            List<User> users = userRepository.findAll();
            log.debug("DB returned result");

            List<UserDto> resultDtos =
                    modelMapper.map(users, new TypeToken<List<UserDto>>() {}.getType());
            log.debug("Mapping from List<User> to List<UserDto>: {}", resultDtos);
            log.debug("Exiting getAllUsers method");

            return resultDtos;
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting getAllUsers method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional(readOnly = true)
    public UserDto getOneUserById(long id) {
        try {
            log.debug("Entering getOneUserById method");
            log.debug("Got {} value as id argument", id);

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("User with id " + id + " is not found"));
            log.debug("User was found");

            UserDto userDto = modelMapper.map(user, UserDto.class);
            log.debug("Mapping from User to UserDto: {}", userDto);
            log.debug("Exiting getOneUserById method");

            return userDto;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting getOneUserById method");

            throw new NotFoundException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting getOneUserById method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional
    public UserDto createUser(CreateUserDto createUserDto) {
        try {
            log.debug("Entering createUser method");
            log.debug("Got {} value as CreateUserDto argument", createUserDto);

            User userEntity = modelMapper.map(createUserDto, User.class);
            log.debug("Mapping from CreateUserDto to User entity {}", userEntity);

            User savedUser = userRepository.save(userEntity);
            UserDto userDtoResult = modelMapper.map(savedUser, UserDto.class);
            log.debug("Mapping from User entity to UserDto {}", userDtoResult);
            log.debug("User entity was saved to DB");
            log.debug("Exiting createUser method");

            return userDtoResult;
        } catch (DataIntegrityViolationException | ConstraintViolationException exc) {
            log.warn("Error has occurred {}", exc.getMessage());
            log.debug("Exiting createUser method");

            throw new ConflictException(exc.getMessage());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting createUser method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional
    public UserDto updateUser(UpdateUserDto dto) {
        try {
            log.debug("Entering updateUser method");
            log.debug("Got {} value as UpdateUserDto argument", dto);

            User userEntity = userRepository.findById(dto.getId())
                    .orElseThrow(() ->
                            new NotFoundException("User with id " + dto.getId() + " is not found"));
            log.debug("User was found");

            String newName = dto.getName() != null
                    ? dto.getName()
                    : userEntity.getName();

            String newEmail = dto.getEmail() != null
                    ? dto.getEmail()
                    : userEntity.getEmail();

            userEntity.setName(newName);
            userEntity.setEmail(newEmail);

            User updatedUser = userRepository.save(userEntity);
            UserDto userDtoResult = modelMapper.map(updatedUser, UserDto.class);
            log.debug("Mapping from User entity to UserDto {}", userDtoResult);
            log.debug("User entity was updated");
            log.debug("Exiting updateUser method");

            return userDtoResult;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting updateUser method");

            throw new NotFoundException(exc.getDescription());
        } catch (DataIntegrityViolationException | ConstraintViolationException exc) {
            log.warn("Error has occurred {}", exc.getMessage());
            log.debug("Exiting updateUser method");

            throw new ConflictException(exc.getMessage());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting updateUser method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional
    public void deleteUser(long id) {
        try {
            log.debug("Entering deleteUser method");
            log.debug("Got {} value as id argument", id);

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("User with id " + id + " is not found"));
            log.debug("User was found");

            userRepository.delete(user);
            log.debug("User entity was deleted");
            log.debug("Exiting deleteUser method");
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting deleteUser method");

            throw new NotFoundException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting deleteUser method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }
}
