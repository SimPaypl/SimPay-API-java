package pl.simpay.api.model.sms;

import lombok.Getter;
import pl.simpay.api.exception.UnsupportedStatusTypeException;

import java.util.Arrays;

@Getter
public enum ServiceStatus {
    NEW("service_new"),
    ACTIVE("service_active"),
    BLOCKED("service_blocked"),
    DELETED("service_deleted"),
    SECOND_VERIFY("service_second_verify"),
    REJECTED("service_rejected"),
    VERIFY("service_verify"),
    ONGOING_REGISTRATION("service_ongoing_registration");

    private final String statusName;

    ServiceStatus(String statusName) {
        this.statusName = statusName;
    }

    public static ServiceStatus getByName(String name) {
        return Arrays.stream(values()).filter(type -> type.statusName.equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new UnsupportedStatusTypeException(name));
    }

}
