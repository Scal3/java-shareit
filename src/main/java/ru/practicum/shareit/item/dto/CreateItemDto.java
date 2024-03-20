package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreateItemDto {

    @Size(max = 255)
    @NotBlank
    private String name;

    @Size(max = 255)
    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private Long requestId;
}
