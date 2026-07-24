package pl.simpay.api.exception;

public class SimPayNetworkException extends ApiException {
    public SimPayNetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
