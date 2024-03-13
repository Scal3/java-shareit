package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import java.util.List;

@Data
public class ItemDtoWithBooking {

    private long id;

    private String name;

    private String description;

    private boolean available;

    private ShortBookingDto lastBooking;

    private ShortBookingDto nextBooking;

    private List<CommentDto> comments;
}
