package pl.simpay.api.model.directBilling.details;

import com.squareup.moshi.Json;
import pl.simpay.api.model.directBilling.commision.CommissionsPercents;
import pl.simpay.api.model.directBilling.ServiceStatus;

import java.time.LocalDateTime;

public record DirectBillingServiceDetailsDTO(
        String id,
        String name,
        String suffix,
        ServiceStatus status,
        Api api,
        Providers providers,
        @Json(name = "commissions") CommissionsPercents commissionsPercents,
        OperatorMaxValues maxValues,
        @Json(name = "created_at") LocalDateTime createdAt
) {
}
