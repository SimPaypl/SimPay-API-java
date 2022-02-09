package pl.simpay.api.exception;

public class UnsupportedOperatorException extends RuntimeException{
    public UnsupportedOperatorException(String operator) {
        super("UNSUPPORTED OPERATOR: " + operator);
    }
}
