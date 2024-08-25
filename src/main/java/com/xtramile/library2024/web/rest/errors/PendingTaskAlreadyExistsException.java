package com.xtramile.library2024.web.rest.errors;

public class PendingTaskAlreadyExistsException extends RuntimeException {

    public PendingTaskAlreadyExistsException(String message) {
        super(message);
    }
}
