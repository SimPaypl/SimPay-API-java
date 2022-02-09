package pl.simpay.api.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(String.format("Api Call Exception: \n%s", message));
    }
}
