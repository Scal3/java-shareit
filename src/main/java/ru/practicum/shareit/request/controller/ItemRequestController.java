package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid CreateItemRequestDto dto
            ) {
        return itemRequestService.createRequest(userId, dto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestWithItemsDto> getUserRequestsById(
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        return itemRequestService.getUserRequestsById(userId);
    }

    @GetMapping(value = "/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestWithItemsDto> getAllUsersRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "15") @Positive int size
    ) {
        return itemRequestService.getAllUsersRequests(userId, from, size);
    }

    @GetMapping(value = "/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestWithItemsDto getOneRequestById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long requestId
    ) {
        return itemRequestService.getOneRequestById(userId, requestId);
    }
}
