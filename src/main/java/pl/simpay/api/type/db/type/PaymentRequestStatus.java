package pl.simpay.api.type.db.type;

public enum PaymentRequestStatus {
    SUCCESS("success"),
    FAILURE("failure");

    private final String status;

    PaymentRequestStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
