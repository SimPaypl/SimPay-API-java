package pl.simpay.api.model.directBilling.transaction;

import com.squareup.moshi.Json;

import java.time.LocalDateTime;

public record DirectBillingTransactionDetailsDTO(int id, TransactionStatus status, String phoneNumber, double value,
                                                 @Json(name = "value_netto") double netValue, Operator operator,
                                                 @Json(name = "notify") Notification notification,
                                                 @Json(name = "created_at") LocalDateTime createdAt,
                                                 @Json(name = "updated_at") LocalDateTime updatedAt) {
}
