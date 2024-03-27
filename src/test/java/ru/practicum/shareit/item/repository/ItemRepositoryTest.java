package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Test
    void findAllByOwnerIdWithBookings_normal_case_then_return_list_of_Item() {
        User userModel = new User();
        userModel.setId(1);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User owner = userRepository.save(userModel);

        Item itemModel1 = new Item();
        itemModel1.setId(1);
        itemModel1.setName("item1");
        itemModel1.setDescription("description 1");
        itemModel1.setAvailable(true);
        itemModel1.setOwner(owner);
        itemRepository.save(itemModel1);

        Item itemModel2 = new Item();
        itemModel2.setId(2);
        itemModel2.setName("item2");
        itemModel2.setDescription("description 2");
        itemModel2.setAvailable(true);
        itemModel2.setOwner(owner);
        itemRepository.save(itemModel2);

        List<Item> items = itemRepository
                .findAllByOwnerIdWithBookings(owner.getId(), Pageable.ofSize(10));

        assertEquals(2, items.size());
    }

    @Test
    void findAllByOwnerIdWithBookings_no_items_in_db_then_return_empty_list() {
        User userModel = new User();
        userModel.setId(1);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User owner = userRepository.save(userModel);

        List<Item> items = itemRepository
                .findAllByOwnerIdWithBookings(owner.getId(), Pageable.ofSize(10));

        assertTrue(items.isEmpty());
    }

    @Test
    void findAllByOwnerIdWithComments_normal_case_then_return_list_of_Item() {
        User userModel = new User();
        userModel.setId(1);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User owner = userRepository.save(userModel);

        Item itemModel1 = new Item();
        itemModel1.setId(1);
        itemModel1.setName("item1");
        itemModel1.setDescription("description 1");
        itemModel1.setAvailable(true);
        itemModel1.setOwner(owner);
        itemRepository.save(itemModel1);

        Item itemModel2 = new Item();
        itemModel2.setId(2);
        itemModel2.setName("item2");
        itemModel2.setDescription("description 2");
        itemModel2.setAvailable(true);
        itemModel2.setOwner(owner);
        itemRepository.save(itemModel2);

        List<Item> items = itemRepository
                .findAllByOwnerIdWithComments(owner.getId(), Pageable.ofSize(10));

        assertEquals(2, items.size());
    }

    @Test
    void findAllByOwnerIdWithComments_no_items_in_db_then_return_empty_list() {
        User userModel = new User();
        userModel.setId(1);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User owner = userRepository.save(userModel);

        List<Item> items = itemRepository
                .findAllByOwnerIdWithComments(owner.getId(), Pageable.ofSize(10));

        assertTrue(items.isEmpty());
    }

    @Test
    void findByAvailableAndKeyword_normal_case_then_return_list_of_Item() {
        User userModel = new User();
        userModel.setId(1);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User owner = userRepository.save(userModel);

        Item itemModel1 = new Item();
        itemModel1.setId(1);
        itemModel1.setName("item1");
        itemModel1.setDescription("description 1");
        itemModel1.setAvailable(true);
        itemModel1.setOwner(owner);
        itemRepository.save(itemModel1);

        Item itemModel2 = new Item();
        itemModel2.setId(2);
        itemModel2.setName("item2");
        itemModel2.setDescription("description 2");
        itemModel2.setAvailable(true);
        itemModel2.setOwner(owner);
        itemRepository.save(itemModel2);

        List<Item> items = itemRepository
                .findByAvailableAndKeyword("description", Pageable.ofSize(10));

        assertEquals(2, items.size());
    }

    @Test
    void findByAvailableAndKeyword_no_items_in_db_then_return_empty_list() {
        List<Item> items = itemRepository
                .findByAvailableAndKeyword("keyword", Pageable.ofSize(10));

        assertTrue(items.isEmpty());
    }
}