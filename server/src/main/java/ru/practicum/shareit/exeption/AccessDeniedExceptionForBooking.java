package ru.practicum.shareit.exeption;

public class AccessDeniedExceptionForBooking extends RuntimeException {
    public AccessDeniedExceptionForBooking(String message) {
        super(message);
    }
}
