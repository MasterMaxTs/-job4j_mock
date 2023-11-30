package ru.job4j.site.controller.rest.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.job4j.site.domain.ErrorMessage;
import ru.job4j.site.exception.AppException;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorMessage> handleException(AppException ex) {
        log.error("App error: status {}, message {}", ex.getStatus(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.valueOf(ex.getStatus()))
                .body(new ErrorMessage(ex.getMessage()));
    }
}
