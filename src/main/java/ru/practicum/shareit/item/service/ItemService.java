package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.exceptionimp.BadRequestException;
import ru.practicum.shareit.exception.exceptionimp.ForbiddenException;
import ru.practicum.shareit.exception.exceptionimp.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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

    private final ItemRequestRepository itemRequestRepository;

    private final ModelMapper modelMapper;

    @Transactional
    public ItemDto createItem(long userId, CreateItemDto dto) {
        log.debug("Entering createItem method: userId = {}, CreateItemDto = {}", userId, dto);

        User owner = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id" + userId + "is not found"));

        Item itemEntity = modelMapper.map(dto, Item.class);
        itemEntity.setOwner(owner);

        if (dto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(dto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(
                            "Request with id" + dto.getRequestId() + "is not found")
                    );

            itemEntity.setRequest(request);
        }

        Item savedItem = itemRepository.save(itemEntity);
        ItemDto itemDtoResult = modelMapper.map(savedItem, ItemDto.class);
        log.info("Mapping from Item entity to ItemDto {}", itemDtoResult);
        log.debug("Exiting createItem method");

        return itemDtoResult;
    }

    @Transactional
    public ItemDto updateItem(UpdateItemDto dto) {
        log.debug("Entering updateItem method: UpdateItemDto = {}", dto);

        userRepository.findById(dto.getUserId()).orElseThrow(
                () -> new NotFoundException("User with id" + dto.getUserId() + "is not found"));

        Item itemEntityForUpdate = itemRepository.findById(dto.getItemId())
                .orElseThrow(() ->
                        new NotFoundException("Item with id " + dto.getItemId() + " is not found"));

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
        log.info("Mapping from Item entity to ItemDto {}", itemDtoResult);
        log.debug("Exiting updateItem method");

        return itemDtoResult;
    }

    @Transactional(readOnly = true)
    public ItemDtoWithBooking getOneItemById(long userId, long itemId) {
        log.debug("Entering getOneItemById method: userId = {}, itemId = {}", userId, itemId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(
                        () -> new NotFoundException("Item with id " + itemId + " is not found"));

        ItemDtoWithBooking itemDto = modelMapper.map(item, ItemDtoWithBooking.class);

        if (item.getOwner().getId() != userId) {
            itemDto.setLastBooking(null);
            itemDto.setNextBooking(null);
        }

        log.info("Mapping from Item to ItemDtoWithBooking: {}", itemDto);
        log.debug("Exiting getOneItemById method");

        return itemDto;
    }

    @Transactional(readOnly = true)
    public List<ItemDtoWithBooking> getOwnersItems(long userId, int from, int size) {
        log.debug("Entering getOwnersItems method: userId = {}, from = {}, size = {}",
                userId, from, size);

        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.findAllByOwnerIdWithComments(userId, pageable);
        itemRepository.findAllByOwnerIdWithBookings(userId, pageable);

        List<ItemDtoWithBooking> resultDtos = modelMapper
                .map(items, new TypeToken<List<ItemDtoWithBooking>>() {}.getType());
        log.info("Mapping from List<Item> to List<ItemDtoWithBooking> {}", resultDtos);
        log.debug("Exiting getOwnersItems method");

        return resultDtos;
    }

    @Transactional(readOnly = true)
    public List<ItemDto> getAvailableItemsBySearchString(String searchString, int from, int size) {
        log.debug("Entering getAvailableItemsBySearchString method: " +
                        "searchString = {}, from = {}, size = {}",
                searchString, from, size);

        if (searchString.isBlank()) return Collections.emptyList();

        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.findByAvailableAndKeyword(searchString, pageable);
        List<ItemDto> resultDtos =
                modelMapper.map(items, new TypeToken<List<ItemDto>>() {}.getType());
        log.info("Mapping from List<Item> to List<ItemDto> {}", resultDtos);
        log.debug("Exiting getAvailableItemsBySearchString method");

        return resultDtos;
    }

    @Transactional
    public CommentDto createComment(long userId, long itemId, CreateCommentDto dto) {
        log.debug("Entering createComment method: userId = {}, itemId = {}, CreateCommentDto = {}",
                    userId, itemId, dto);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id " + userId + " is not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(
                        () -> new NotFoundException("Item with id " + itemId + " is not found"));

        bookingRepository.findAllByUserIdAndItemIdAndStatusAndBookingDateEndBefore(
                        userId,
                        itemId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now()
                )
                .stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Booking does not exist"));

        Comment comment = modelMapper.map(dto, Comment.class);
        comment.setCreated(LocalDateTime.now());
        comment.setUser(user);
        comment.setItem(item);

        Comment savedComment = commentRepository.save(comment);
        CommentDto resultDto = modelMapper.map(savedComment, CommentDto.class);
        log.info("Mapping from Comment to CommentDto: {}", resultDto);
        log.debug("Exiting createComment method");

        return resultDto;
    }
}
