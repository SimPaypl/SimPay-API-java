package pl.simpay.api.exception;

public class SimPayTimeoutException extends SimPayNetworkException {
    public SimPayTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
