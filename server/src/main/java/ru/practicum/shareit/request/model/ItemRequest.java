package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "requestor_id", nullable = false)
    private Long requestorId;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime created;

    @OneToMany(mappedBy = "request")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Item> items;

    public ItemRequest(Long id, String description, Long requestorId, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.requestorId = requestorId;
        this.created = created;
    }
}
