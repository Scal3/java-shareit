package ru.practicum.shareit.user.model;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.review.model.Review;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class User {

    private long id;

    private String email;

    private String name;

    private List<Item> items;

    private List<Review> reviews;
}
