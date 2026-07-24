package pl.simpay.api.exception;

import java.util.List;
import java.util.Map;

public final class SimPayValidationException extends SimPayApiException {
    private final Map<String, List<String>> errors;
    public SimPayValidationException(String message, String errorCode, String body, Map<String, List<String>> errors) {
        super(message, 422, errorCode, body);
        this.errors = Map.copyOf(errors);
    }
    public Map<String, List<String>> getErrors() { return errors; }
}
