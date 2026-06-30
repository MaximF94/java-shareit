package ru.practicum.shareit.item.dto;

import lombok.Data;


@Data
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;

    public ItemDto(Long id, String name, String description, Boolean available, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
