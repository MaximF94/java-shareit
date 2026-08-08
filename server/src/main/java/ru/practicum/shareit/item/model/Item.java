package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "available")
    private Boolean available;

    @Column(name = "owner")
    private Long owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ItemRequest request;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Comment> comments;

    public Item(Long id, String name, String description, Boolean available, Long owner, ItemRequest request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }
}
