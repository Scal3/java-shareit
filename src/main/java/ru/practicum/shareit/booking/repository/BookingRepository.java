package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"user", "item"})
    List<Booking> findAllByUserIdOrderByBookingDateEndDesc(long userId);

    @EntityGraph(attributePaths = {"user", "item"})
    List<Booking> findAllByUserIdAndBookingDateEndBeforeOrderByBookingDateEndDesc(long userId, LocalDateTime currentDate);

    @EntityGraph(attributePaths = {"user", "item"})
    List<Booking> findAllByUserIdAndBookingDateStartBeforeAndBookingDateEndAfterOrderByBookingDateEndDesc(long userId, LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = {"user", "item"})
    List<Booking> findAllByUserIdAndStatusOrderByBookingDateEndDesc(long userId, BookingStatus status);

    @EntityGraph(attributePaths = {"user", "item"})
    List<Booking> findAllByUserIdAndBookingDateStartAfterOrderByBookingDateEndDesc(long userId, LocalDateTime currentDate);

    @EntityGraph(attributePaths = {"user", "item"})
    List<Booking> findAllByItemOwnerIdOrderByBookingDateEndDesc(long ownerId);

    @EntityGraph(attributePaths = {"user", "item"})
    List<Booking> findAllByItemOwnerIdAndBookingDateEndBeforeOrderByBookingDateEndDesc(long ownerId, LocalDateTime currentDate);

    @EntityGraph(attributePaths = {"user", "item"})
    List<Booking> findAllByItemOwnerIdAndBookingDateStartBeforeAndBookingDateEndAfterOrderByBookingDateEndDesc(long ownerId, LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = {"user", "item"})
    List<Booking> findAllByItemOwnerIdAndStatusOrderByBookingDateEndDesc(long ownerId, BookingStatus status);

    @EntityGraph(attributePaths = {"user", "item"})
    List<Booking> findAllByItemOwnerIdAndBookingDateStartAfterOrderByBookingDateEndDesc(long ownerId, LocalDateTime currentDate);

    List<Booking> findAllByUserIdAndItemIdAndStatusAndBookingDateEndBefore(long userId, long itemId, BookingStatus status, LocalDateTime dateTime);
}
