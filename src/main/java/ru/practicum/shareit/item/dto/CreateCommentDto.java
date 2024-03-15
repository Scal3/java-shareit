package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CreateCommentDto {

    @NotBlank
    @Size(min = 5, max = 255)
    private String text;
}
