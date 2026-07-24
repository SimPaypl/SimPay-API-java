package pl.simpay.api.model.directBilling;

import com.squareup.moshi.Json;
import java.time.LocalDateTime;

public record DirectBillingServiceDTO(
        String id,
        String name,
        String suffix,
        ServiceStatus status,
        @Json(name = "created_at") LocalDateTime createdAt
) {
}
