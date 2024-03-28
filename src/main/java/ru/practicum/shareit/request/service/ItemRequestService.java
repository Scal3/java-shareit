package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.exceptionimp.NotFoundException;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Transactional
    public ItemRequestDto createRequest(long userId, CreateItemRequestDto dto) {
        log.debug("Entering createRequest method: userId = {}, CreateItemRequestDto = {}",
                userId, dto);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id " + userId + " is not found"));

        ItemRequest request = modelMapper.map(dto, ItemRequest.class);
        request.setCreated(LocalDateTime.now());
        request.setUser(user);

        ItemRequest savedRequest = itemRequestRepository.save(request);
        ItemRequestDto itemRequestDto = modelMapper.map(savedRequest, ItemRequestDto.class);
        log.info("Mapping from ItemRequest entity to ItemRequestDto {}", itemRequestDto);
        log.debug("Exiting createRequest method");

        return itemRequestDto;
    }

    @Transactional(readOnly = true)
    public List<ItemRequestWithItemsDto> getUserRequestsById(long userId) {
        log.debug("Entering getUserRequestsById method: userId = {}", userId);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id " + userId + " is not found"));

        List<ItemRequest> itemRequests =
                itemRequestRepository.findAllByUserOrderByCreatedDesc(user);
        List<ItemRequestWithItemsDto> requestDtos =
                modelMapper.map(itemRequests, new TypeToken<List<ItemRequestWithItemsDto>>() {}.getType());
        log.info("Mapping from List<ItemRequest> to List<ItemRequestWithItemsDto> {}", requestDtos);
        log.debug("Exiting getUserRequestsById method");

        return requestDtos;
    }

    @Transactional(readOnly = true)
    public List<ItemRequestWithItemsDto> getAllUsersRequests(long userId, int from, int size) {
        log.debug("Entering getAllUsersRequests method: userId ={}, from = {}, size = {}",
                userId, from, size);

        Pageable pageable = PageRequest.of(from, size);
        List<ItemRequest> itemRequests =
                itemRequestRepository.findAllByUserIdNotOrderByCreatedDesc(userId, pageable);
        List<ItemRequestWithItemsDto> requestDtos =
                modelMapper.map(itemRequests, new TypeToken<List<ItemRequestWithItemsDto>>() {}.getType());
        log.info("Mapping from List<ItemRequest> to List<ItemRequestWithItemsDto> {}", requestDtos);
        log.debug("Exiting getAllUsersRequests method");

        return requestDtos;
    }

    @Transactional(readOnly = true)
    public ItemRequestWithItemsDto getOneRequestById(long userId, long requestId) {
        log.debug("Entering getOneRequestById method: userId = {}, requestId = {}",
                userId, requestId);

        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id " + userId + " is not found"));

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(
                        () -> new NotFoundException("Request with id " + requestId + " is not found"));

        ItemRequestWithItemsDto requestDto =
                modelMapper.map(itemRequest, ItemRequestWithItemsDto.class);
        log.info("Mapping ItemRequest to ItemRequestWithItemsDto {}", requestDto);
        log.debug("Exiting getOneRequestById method");

        return requestDto;
    }
}

