package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwner(Long owner);

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.comments c LEFT JOIN FETCH c.author WHERE i.id = :id")
    Optional<Item> findByIdWithComments(Long id);

    @Query("SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.comments c " +
            "LEFT JOIN FETCH c.author WHERE LOWER(i.name)" +
            " LIKE LOWER(CONCAT('%', :text, '%'))" +
            " OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<Item> searchWithComments(String text);

}
