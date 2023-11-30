package ru.job4j.site.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import ru.job4j.site.exception.ClientAppException;
import ru.job4j.site.exception.ServerAppException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    private static final HttpStatus.Series CLIENT_ERROR = HttpStatus.Series.CLIENT_ERROR;
    private static final HttpStatus.Series SERVER_ERROR = HttpStatus.Series.SERVER_ERROR;
    private static final HttpStatus NOT_FOUND = HttpStatus.NOT_FOUND;


    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().series() == CLIENT_ERROR
                || response.getStatusCode().series() == SERVER_ERROR;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus httpStatus = response.getStatusCode();
        if (httpStatus.series() == SERVER_ERROR) {
            throw new ServerAppException(
                    getErrorMessageFromResponse(response),
                    httpStatus.value());
        }
        if (httpStatus.series() == CLIENT_ERROR) {
            String errorMessage = httpStatus == NOT_FOUND
                    ? "ID not found!" : getErrorMessageFromResponse(response);
            throw new ClientAppException(errorMessage, httpStatus.value());
        }
    }

    private String getErrorMessageFromResponse(ClientHttpResponse response) throws IOException {
        return new String(
                response.getBody().readAllBytes(), StandardCharsets.UTF_8);
    }
}
