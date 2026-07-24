package pl.simpay.api.exception;

public final class InvalidBlikTicketException extends SimPayApiException {
    public InvalidBlikTicketException(String message, int statusCode, String errorCode, String body) {
        super(message, statusCode, errorCode, body);
    }
}
