package pl.simpay.api.exception;

public class SimPayApiException extends ApiException {
    private final int statusCode;
    private final String errorCode;
    private final String responseBody;

    public SimPayApiException(String message, int statusCode, String errorCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
