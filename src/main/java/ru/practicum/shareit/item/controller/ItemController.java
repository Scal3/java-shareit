package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid CreateItemDto dto
    ) {
        return itemService.createItem(userId, dto);
    }

    @PatchMapping(
            value = "/{itemId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable @Positive long itemId,
            @RequestBody @Valid UpdateItemDto dto
    ) {
        dto.setUserId(userId);
        dto.setItemId(itemId);

        return itemService.updateItem(dto);
    }

    @GetMapping(
            value = "/{itemId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getOneItemById(@PathVariable @Positive long itemId) {
        return itemService.getOneItemById(itemId);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getOwnersItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getOwnersItems(userId);
    }

    @GetMapping(
            value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAvailableItemsBySearchString(@RequestParam String text) {
        return itemService.getAvailableItemsBySearchString(text);
    }
}
