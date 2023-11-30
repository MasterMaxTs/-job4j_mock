package ru.job4j.site.exception;

public class ServerAppException extends AppException {
    public ServerAppException(String message, int status) {
        super(message, status);
    }
}
