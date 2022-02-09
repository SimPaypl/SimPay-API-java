package pl.simpay.api.model.sms;

import com.squareup.moshi.Json;

import java.time.LocalDateTime;

public record SmsServiceDTO(
        int id,
        SmsServiceType type,
        ServiceStatus status,
        String name,
        String prefix,
        String suffix,
        boolean adult,
        @Json(name = "created_at") LocalDateTime createdAt
) {
}
