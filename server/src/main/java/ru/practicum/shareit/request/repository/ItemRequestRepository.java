package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT r FROM ItemRequest r LEFT JOIN FETCH r.items WHERE r.id = :id")
    Optional<ItemRequest> findByIdWithItems(Long id);

    @Query("SELECT r FROM ItemRequest r LEFT JOIN FETCH r.items WHERE r.requestorId = :userId ORDER BY r.created DESC")
    Collection<ItemRequest> findAllByUserId(Long userId);

    @Query("SELECT r FROM ItemRequest r LEFT JOIN FETCH r.items WHERE r.requestorId != :userId ORDER BY r.created DESC")
    Collection<ItemRequest> findAllOtherRequests(Long userId);
}
