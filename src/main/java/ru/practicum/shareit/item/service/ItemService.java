package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.exceptionimp.BadRequestException;
import ru.practicum.shareit.exception.exceptionimp.ForbiddenException;
import ru.practicum.shareit.exception.exceptionimp.InternalServerException;
import ru.practicum.shareit.exception.exceptionimp.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final ModelMapper modelMapper;

    private final UserService userService;

    @Transactional
    public ItemDto createItem(long userId, CreateItemDto dto) {
        try {
            log.debug("Entering createItem method");
            log.debug("Got {} value as CreateItemDto argument", dto);

            UserDto userDto = userService.getOneUserById(userId);
            log.debug("User was found");

            User owner = modelMapper.map(userDto, User.class);
            log.debug("Mapping from UserDto to User {}", owner);

            Item itemEntity = modelMapper.map(dto, Item.class);
            itemEntity.setOwner(owner);
            log.debug("Mapping from CreateItemDto to Item entity {}", itemEntity);

            Item savedItem = itemRepository.save(itemEntity);
            ItemDto itemDtoResult = modelMapper.map(savedItem, ItemDto.class);
            log.debug("Mapping from Item entity to ItemDto {}", itemDtoResult);
            log.debug("Item entity was saved to DB");
            log.debug("Exiting createItem method");

            return itemDtoResult;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting createItem method");

            throw new NotFoundException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting createItem method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional
    public ItemDto updateItem(UpdateItemDto dto) {
        try {
            log.debug("Entering updateItem method");
            log.debug("Got {} value as UpdateItemDto argument", dto);

            UserDto userDto = userService.getOneUserById(dto.getUserId());
            log.debug("User was found");

            User owner = modelMapper.map(userDto, User.class);
            log.debug("Mapping from UserDto to User {}", owner);

            Item itemEntityForUpdate = itemRepository.findById(dto.getItemId())
                    .orElseThrow(() ->
                            new NotFoundException("Item with id " + dto.getItemId() + " is not found"));
            log.debug("Item was found");

            if (itemEntityForUpdate.getOwner().getId() != dto.getUserId())
                throw new ForbiddenException("Only owner can update its items");

            String newName = dto.getName() != null
                    ? dto.getName()
                    : itemEntityForUpdate.getName();
            String newDescription = dto.getDescription() != null
                    ? dto.getDescription()
                    : itemEntityForUpdate.getDescription();
            boolean newAvailable = dto.getAvailable() != null
                    ? dto.getAvailable()
                    : itemEntityForUpdate.isAvailable();

            itemEntityForUpdate.setName(newName);
            itemEntityForUpdate.setDescription(newDescription);
            itemEntityForUpdate.setAvailable(newAvailable);

            Item savedItem = itemRepository.save(itemEntityForUpdate);
            ItemDto itemDtoResult = modelMapper.map(savedItem, ItemDto.class);
            log.debug("Mapping from Item entity to ItemDto {}", itemDtoResult);
            log.debug("Item entity was updated");
            log.debug("Exiting updateItem method");

            return itemDtoResult;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting updateItem method");

            throw new NotFoundException(exc.getDescription());
        } catch (ForbiddenException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting updateItem method");

            throw new ForbiddenException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting updateItem method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional(readOnly = true)
    public ItemDtoWithBooking getOneItemById(long userId, long itemId) {
        try {
            log.debug("Entering getOneItemById method");
            log.debug("Got {} value as userId and {} value as itemId", userId, itemId);

            Item item = itemRepository.findById(itemId)
                    .orElseThrow(
                            () -> new NotFoundException("Item with id " + itemId + " is not found"));
            log.debug("Item was found");

            ItemDtoWithBooking itemDto = modelMapper.map(item, ItemDtoWithBooking.class);
            log.debug("Mapping from Item to ItemDtoWithBooking: {}", itemDto);

            if (item.getOwner().getId() != userId) {
                itemDto.setLastBooking(null);
                itemDto.setNextBooking(null);
            }

            log.debug("Exiting getOneItemById method");

            return itemDto;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting getOneItemById method");

            throw new NotFoundException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting getOneItemById method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional(readOnly = true)
    public List<ItemDtoWithBooking> getOwnersItems(long userId) {
        try {
            log.debug("Entering getOwnersItems method");
            log.debug("Got {} value as userId argument", userId);

            List<Item> items = itemRepository.findAllByOwnerIdWithComments(userId);
            items = itemRepository.findAllByOwnerIdWithBookings(userId);

            List<ItemDtoWithBooking> resultDtos = modelMapper
                            .map(items, new TypeToken<List<ItemDtoWithBooking>>() {}.getType());
            log.debug("DB returned result");
            log.debug("Mapping from List<Item> to List<ItemDtoWithBooking> {}", resultDtos);
            log.debug("Exiting getOwnersItems method");

            return resultDtos;
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting getOwnersItems method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional(readOnly = true)
    public List<ItemDto> getAvailableItemsBySearchString(String searchString) {
        try {
            log.debug("Entering getAvailableItemsBySearchString method");
            log.debug("Got {} value as searchString argument", searchString);

            if (searchString.isBlank()) return Collections.emptyList();

            List<Item> items = itemRepository.findByAvailableAndKeyword(searchString);
            List<ItemDto> resultDtos =
                    modelMapper.map(items, new TypeToken<List<ItemDto>>() {}.getType());
            log.debug("DB returned result");
            log.debug("Mapping from List<Item> to List<ItemDto> {}", resultDtos);
            log.debug("Exiting getAvailableItemsBySearchString method");

            return resultDtos;
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting getAvailableItemsBySearchString method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional
    public CommentDto createComment(long userId, long itemId, CreateCommentDto dto) {
        try {
            log.debug("Entering createComment method");
            log.debug("Got {} value as userId, {} value as itemId and {} value as CreateCommentDto",
                    userId, itemId, dto);

            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new NotFoundException("User with id " + userId + " is not found"));
            log.debug("User was found");

            Item item = itemRepository.findById(itemId)
                    .orElseThrow(
                            () -> new NotFoundException("Item with id " + itemId + " is not found"));
            log.debug("Item was found");

            bookingRepository.findAllByUserIdAndItemIdAndStatusAndBookingDateEndBefore(
                            userId,
                            itemId,
                            BookingStatus.APPROVED,
                            LocalDateTime.now()
                    )
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Booking does not exist"));
            log.debug("Booking was found");

            Comment comment = modelMapper.map(dto, Comment.class);
            comment.setCreated(LocalDateTime.now());
            comment.setUser(user);
            comment.setItem(item);

            Comment savedComment = commentRepository.save(comment);
            CommentDto resultDto = modelMapper.map(savedComment, CommentDto.class);
            log.debug("Mapping from Comment to CommentDto: {}", resultDto);
            log.debug("Exiting createComment method");

            return resultDto;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting createComment method");

            throw new NotFoundException(exc.getDescription());
        } catch (BadRequestException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting createComment method");

            throw new BadRequestException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting createComment method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }
}
