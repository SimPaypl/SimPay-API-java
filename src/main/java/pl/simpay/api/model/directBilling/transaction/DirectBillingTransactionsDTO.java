package pl.simpay.api.model.directBilling.transaction;

import com.squareup.moshi.Json;

import java.time.LocalDateTime;

public record DirectBillingTransactionsDTO(
        int id,
        TransactionStatus status,
        double value,
        @Json(name = "value_netto") double netValue,
        Operator operator,
        @Json(name = "created_at") LocalDateTime createdAt,
        @Json(name = "updated_at") LocalDateTime updatedAt

) {
}
