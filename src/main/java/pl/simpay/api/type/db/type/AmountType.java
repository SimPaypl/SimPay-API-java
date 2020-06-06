package pl.simpay.api.type.db.type;

public enum AmountType {
    AMOUNT("amount"),
    AMOUNT_TYPE("amount_gross"),
    AMOUNT_REQUIRED("amount_required");

    private final String type;

    AmountType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
