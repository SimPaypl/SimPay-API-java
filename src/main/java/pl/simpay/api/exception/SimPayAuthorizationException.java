package pl.simpay.api.exception;

public final class SimPayAuthorizationException extends SimPayApiException {
    public SimPayAuthorizationException(String message, int statusCode, String errorCode, String body) {
        super(message, statusCode, errorCode, body);
    }
}
