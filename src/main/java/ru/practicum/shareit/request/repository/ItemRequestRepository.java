package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @EntityGraph(attributePaths = "items")
    List<ItemRequest> findAllByUserOrderByCreatedDesc(User user);

//    @EntityGraph(attributePaths = "items")
//    List<ItemRequest> findAllByOrderByCreatedDesc(Pageable pageable);

    @EntityGraph(attributePaths = "items")
    List<ItemRequest> findAllByUserIdNotOrderByCreatedDesc(long userId, Pageable pageable);
}
