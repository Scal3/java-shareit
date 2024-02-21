package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class CreateUserDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;
}
