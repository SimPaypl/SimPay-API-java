package pl.simpay.api.model.directBilling;

import lombok.Getter;
import pl.simpay.api.exception.UnsupportedStatusTypeException;

import java.util.Arrays;

public enum ServiceStatus {
    NEW("service_db_new"),
    ACTIVE("service_active"),
    REJECTED("service_db_rejected"),
    ONGOING_REGISTRATION("service_db_ongoing_registration");

    @Getter
    private final String statusName;

    ServiceStatus(String statusName) {
        this.statusName = statusName;
    }

    public static ServiceStatus getByName(String name) {
        return Arrays.stream(values()).filter(type -> type.statusName.equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new UnsupportedStatusTypeException(name));
    }
}
