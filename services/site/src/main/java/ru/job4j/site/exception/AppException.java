package ru.job4j.site.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class AppException extends RuntimeException {

    private final String message;
    private final int status;
}
