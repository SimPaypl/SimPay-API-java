package pl.simpay.api.model.directBilling.transaction;

import com.squareup.moshi.Json;

import java.time.LocalDateTime;

public record Notification(
        @Json(name = "is_send") boolean isSend,
        @Json(name = "last_send_at") LocalDateTime lastSendAt,
        int count
) {
}
