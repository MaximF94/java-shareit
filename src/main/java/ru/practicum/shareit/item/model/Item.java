package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;
    Long owner;
    Long requestId;

    public Item(Long id, String name, String description, Boolean available, Long owner, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.requestId = requestId;
    }
}
