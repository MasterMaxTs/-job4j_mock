package ru.job4j.site.exception;

public class ClientAppException extends AppException {
    public ClientAppException(String message, int status) {
        super(message, status);
    }
}
