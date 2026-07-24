package pl.simpay.api.model.directBilling.transaction;

import com.squareup.moshi.Json;

import java.time.LocalDateTime;

public record DirectBillingTransactionDetailsDTO(
        String id,
        TransactionStatus status,
        String phoneNumber,
        String control,
        double value,
        @Json(name = "value_net") double netValue,
        @Json(name = "value_created") double valueCreated,
        @Json(name = "value_created_type") String valueCreatedType,
        Operator operator,
        @Json(name = "notify") Notification notification,
        Double score,
        @Json(name = "created_at") LocalDateTime createdAt,
        @Json(name = "updated_at") LocalDateTime updatedAt
) {
    /** Compatibility constructor for the response shape exposed before version 3. */
    public DirectBillingTransactionDetailsDTO(
            String id,
            TransactionStatus status,
            String phoneNumber,
            double value,
            double netValue,
            Operator operator,
            Notification notification,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this(id, status, phoneNumber, null, value, netValue, 0, null, operator, notification, null, createdAt, updatedAt);
    }
}
