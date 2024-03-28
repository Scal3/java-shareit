package ru.practicum.shareit.request.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestRepositoryTest {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    @Test
    void findAllByUserOrderByCreatedDesc_no_requests_in_db_then_return_empty_list() {
        User userModel = new User();
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User user = userRepository.save(userModel);

        List<ItemRequest> itemRequests =
                itemRequestRepository.findAllByUserOrderByCreatedDesc(user);

        assertTrue(itemRequests.isEmpty());
    }

    @Test
    void findAllByUserOrderByCreatedDesc_normal_case_then_return_list_of_ItemRequest() {
        User userModel = new User();
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User user = userRepository.save(userModel);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1);
        itemRequest1.setUser(user);
        itemRequest1.setDescription("some description 1");
        itemRequest1.setCreated(LocalDateTime.now().minusDays(1));

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest1.setId(2);
        itemRequest2.setUser(user);
        itemRequest2.setDescription("some description 2");
        itemRequest2.setCreated(LocalDateTime.now());

        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);

        List<ItemRequest> itemRequests =
                itemRequestRepository.findAllByUserOrderByCreatedDesc(user);

        assertEquals(2, itemRequests.size());
        assertEquals(itemRequest2.getId(), itemRequests.get(0).getId());
    }

    @Test
    void findAllByUserIdNotOrderByCreatedDesc_normal_case_then_return_list_of_ItemRequest() {
        User userModel0 = new User();
        userModel0.setId(1);
        userModel0.setName("00user00");
        userModel0.setEmail("00user@email.com");
        User justUser = userRepository.save(userModel0);

        User userModel = new User();
        userModel.setId(2);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User booker = userRepository.save(userModel);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1);
        itemRequest1.setUser(booker);
        itemRequest1.setDescription("some description 1");
        itemRequest1.setCreated(LocalDateTime.now().minusDays(1));

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest1.setId(2);
        itemRequest2.setUser(booker);
        itemRequest2.setDescription("some description 2");
        itemRequest2.setCreated(LocalDateTime.now());

        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);

        List<ItemRequest> itemRequests =
                itemRequestRepository.findAllByUserIdNotOrderByCreatedDesc(
                        justUser.getId(), Pageable.ofSize(10));

        assertEquals(2, itemRequests.size());
        assertEquals(itemRequest2.getId(), itemRequests.get(0).getId());
    }

    @Test
    void findAllByUserIdNotOrderByCreatedDesc_no_requests_in_db_then_return_empty_list() {
        User userModel = new User();
        userModel.setId(1);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        userRepository.save(userModel);

        List<ItemRequest> itemRequests =
                itemRequestRepository
                        .findAllByUserIdNotOrderByCreatedDesc(1, Pageable.ofSize(1));

        assertTrue(itemRequests.isEmpty());
    }
}