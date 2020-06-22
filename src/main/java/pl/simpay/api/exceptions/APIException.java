package pl.simpay.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.Response;

@Getter
@AllArgsConstructor
public class APIException extends RuntimeException {
    private final Response response;
}
