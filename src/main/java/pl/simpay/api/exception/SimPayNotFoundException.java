package pl.simpay.api.exception;

public final class SimPayNotFoundException extends SimPayApiException {
    public SimPayNotFoundException(String message, String errorCode, String body) {
        super(message, 404, errorCode, body);
    }
}
