package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptionimp.BadRequestException;
import ru.practicum.shareit.exception.exceptionimp.ForbiddenException;
import ru.practicum.shareit.exception.exceptionimp.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private long id;

    private final Map<Long, Item> inMemoryItems;

    private final ItemMapper itemMapper;

    private final UserMapper userMapper;

    private final UserService userService;

    public ItemDto createItem(long userId, ItemDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BadRequestException("name field is null or blank");
        }

        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new BadRequestException("description field is null or blank");
        }

        if (dto.getAvailable() == null) {
            throw new BadRequestException("available field is null");
        }

        User owner = userMapper.fromDto(userService.getOneUserById(userId));
        dto.setId(++id);
        Item item = itemMapper.fromDto(dto);
        item.setOwner(owner);

        inMemoryItems.put(id, item);

        return itemMapper.toDto(item);
    }

    public ItemDto updateItem(long userId, ItemDto dto) {
        Item itemForUpdate = inMemoryItems.get(dto.getId());

        if (itemForUpdate == null)
            throw new NotFoundException("Item with id " + dto.getId() + " is not found");

        if (itemForUpdate.getOwner().getId() != userId)
            throw new ForbiddenException("Only owner can update its items");

        String newName = dto.getName() != null
                ? dto.getName()
                : itemForUpdate.getName();
        String newDescription = dto.getDescription() != null
                ? dto.getDescription()
                : itemForUpdate.getDescription();
        boolean newAvailable = dto.getAvailable() != null
                ? dto.getAvailable()
                : itemForUpdate.isAvailable();

        itemForUpdate.setName(newName);
        itemForUpdate.setDescription(newDescription);
        itemForUpdate.setAvailable(newAvailable);
        inMemoryItems.replace(dto.getId(), itemForUpdate);

        return itemMapper.toDto(itemForUpdate);
    }

    public ItemDto getOneItemById(long id) {
        Item item = inMemoryItems.get(id);

        if (item == null)
            throw new NotFoundException("Item with id " + id + " is not found");

        return itemMapper.toDto(item);
    }

    public List<ItemDto> getOwnersItems(long userId) {
        return inMemoryItems.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> getAvailableItemsBySearchString(String searchString) {
        if (searchString.isBlank()) return Collections.emptyList();

        return inMemoryItems.values().stream()
                .filter(item ->
                        (item.getName().toLowerCase().contains(searchString.toLowerCase())
                                || item.getDescription().toLowerCase().contains(searchString.toLowerCase()))
                                && item.isAvailable())
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }
}
