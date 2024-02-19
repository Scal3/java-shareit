package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.review.model.Review;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {

    private long id;

    private String name;

    private String description;

    private boolean available;

    private User owner;

    private List<Review> reviews;
}
