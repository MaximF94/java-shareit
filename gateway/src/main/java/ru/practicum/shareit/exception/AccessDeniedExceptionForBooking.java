package ru.practicum.shareit.exception;

public class AccessDeniedExceptionForBooking extends RuntimeException {
    public AccessDeniedExceptionForBooking(String message) {
        super(message);
    }
}
