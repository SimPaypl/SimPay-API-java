package pl.simpay.api.exception;

public class UnsupportedStatusTypeException extends RuntimeException {
    public UnsupportedStatusTypeException(String message) {
        super("UNSUPPORTED STATUS TYPE : " + message);
    }
}
