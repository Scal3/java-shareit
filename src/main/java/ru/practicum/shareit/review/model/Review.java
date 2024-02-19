package ru.practicum.shareit.review.model;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Data
public class Review {

    private long id;

    private String text;

    private User user;

    private Item item;
}
