package pl.simpay.api.model.directBilling.transaction;

import lombok.Getter;
import pl.simpay.api.exception.UnsupportedOperatorException;

import java.util.Arrays;

public enum TransactionStatus {
    NEW("transaction_db_new"),
    PAYED("transaction_db_payed"),
    CONFIRMED("transaction_db_confirmed"),
    REJECTED("transaction_db_rejected"),
    CANCELED("transaction_db_canceled"),
    ERROR("transaction_db_generate_error");

    @Getter
    private final String statusName;

    TransactionStatus(String statusName) {
        this.statusName = statusName;
    }

    public static TransactionStatus getByName(String name) {
        return Arrays.stream(values()).filter(operator -> operator.getStatusName().equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new UnsupportedOperatorException(name));
    }
}
