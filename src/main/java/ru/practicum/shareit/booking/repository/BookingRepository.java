package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :userId " +
            "ORDER BY b.start DESC")
    Collection<Booking> findAllByBookerId(Long userId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :userId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<Booking> findCurrentByBookerId(Long userId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :userId " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<Booking> findPastByBookerId(Long userId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :userId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<Booking> findFutureByBookerId(Long userId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :userId " +
            "AND b.status = 'WAITING' " +
            "ORDER BY b.start DESC")
    Collection<Booking> findWaitingByBookerId(Long userId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :userId " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.start DESC")
    Collection<Booking> findRejectedByBookerId(Long userId);


    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.item.owner = :ownerId " +
            "ORDER BY b.start DESC")
    Collection<Booking> findAllByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.item.owner = :ownerId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<Booking> findCurrentByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.item.owner = :ownerId " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<Booking> findPastByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.item.owner = :ownerId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Collection<Booking> findFutureByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.item.owner = :ownerId " +
            "AND b.status = 'WAITING' " +
            "ORDER BY b.start DESC")
    Collection<Booking> findWaitingByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.item.owner = :ownerId " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.start DESC")
    Collection<Booking> findRejectedByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.end < CURRENT_TIMESTAMP")
    List<Booking> findCompletedBookingsByUserAndItem(Long userId, Long itemId);

    @Query("SELECT b.item.id, MAX(b.end) FROM Booking b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.status = 'APPROVED' " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "GROUP BY b.item.id")
    List<Object[]> findLastBookingEndsForItems(List<Long> itemIds);

    @Query("SELECT b.item.id, MIN(b.start) FROM Booking b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.status = 'APPROVED' " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "GROUP BY b.item.id")
    List<Object[]> findNextBookingStartsForItems(List<Long> itemIds);
}
