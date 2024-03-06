package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class UpdateItemDto {

    private Long userId;

    private Long itemId;

    private String name;

    private String description;

    private Boolean available;
}
