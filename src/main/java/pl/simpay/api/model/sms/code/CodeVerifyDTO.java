package pl.simpay.api.model.sms.code;

import com.squareup.moshi.Json;

import java.time.LocalDateTime;

public record CodeVerifyDTO(
        boolean used,
        String code,
        boolean test,
        int from,
        long number,
        double value,
        @Json(name = "used_at") LocalDateTime usedAt
) {
}
