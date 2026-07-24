package pl.simpay.api.model.directBilling.transaction;

import com.squareup.moshi.Json;

public record DirectBillingTransactionNotificationDTO(
        String id,
        String serviceId,
        TransactionStatus status,
        Values values,
        DirectBillingNotificationReturns returns,
        String control,
        @Json(name = "number_from") String numberFrom,
        int provider,
        String signature
) {
}
