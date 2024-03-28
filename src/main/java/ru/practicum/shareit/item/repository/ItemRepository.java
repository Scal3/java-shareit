package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.bookings b LEFT JOIN i.comments c WHERE i.owner.id = ?1")
    List<Item> findAllByOwnerIdWithBookings(long userId, Pageable pageable);

    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.comments c LEFT JOIN i.bookings b WHERE i.owner.id = ?1")
    List<Item> findAllByOwnerIdWithComments(long userId, Pageable pageable);

    @Query(value = "SELECT * FROM items WHERE available = true AND (name ILIKE %:keyword% OR description ILIKE %:keyword%)", nativeQuery = true)
    List<Item> findByAvailableAndKeyword(@Param("keyword") String keyword, Pageable pageable);
}
