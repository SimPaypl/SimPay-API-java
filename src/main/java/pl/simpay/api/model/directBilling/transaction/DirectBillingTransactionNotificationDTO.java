package pl.simpay.api.model.directBilling.transaction;

import com.squareup.moshi.Json;
import pl.simpay.api.model.request.RedirectURL;

public record DirectBillingTransactionNotificationDTO(
        String id,
        String serviceId,
        TransactionStatus status,
        Values values,
        RedirectURL returns,
        String control,
        @Json(name = "number_from") String numberFrom,
        int provider,
        String signature
) {
}
