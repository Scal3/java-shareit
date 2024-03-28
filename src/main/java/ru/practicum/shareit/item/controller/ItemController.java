package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid CreateItemDto dto
    ) {
        return itemService.createItem(userId, dto);
    }

    @PatchMapping(value = "/{itemId}")
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

    @GetMapping(value = "/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDtoWithBooking getOneItemById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable @Positive long itemId
    ) {
        return itemService.getOneItemById(userId, itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDtoWithBooking> getOwnersItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "15") @Positive int size
    ) {
        return itemService.getOwnersItems(userId, from, size);
    }

    @GetMapping(value = "/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAvailableItemsBySearchString(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "15") @Positive int size
    ) {
        return itemService.getAvailableItemsBySearchString(text, from, size);
    }

    @PostMapping(value = "/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto createComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable @Positive long itemId,
            @RequestBody @Valid CreateCommentDto dto
    ) {
        return itemService.createComment(userId, itemId, dto);
    }
}
