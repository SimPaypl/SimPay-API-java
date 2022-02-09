package pl.simpay.api.model.directBilling.transaction;

import com.squareup.moshi.Json;
import pl.simpay.api.model.request.RedirectURL;

public record DirectBillingTransactionNotificationDTO(
        int id,
        @Json(name = "service_id") int serviceId,
        TransactionStatus status,
        Values values,
        RedirectURL returns,
        String control,
        String number,
        int provider,
        String signature
) {
}
