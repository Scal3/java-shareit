package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(long userId);

    @Query(value = "SELECT * FROM items WHERE available = true AND (name ILIKE %:keyword% OR description ILIKE %:keyword%)", nativeQuery = true)
    List<Item> findByAvailableAndKeyword(@Param("keyword") String keyword);
}
