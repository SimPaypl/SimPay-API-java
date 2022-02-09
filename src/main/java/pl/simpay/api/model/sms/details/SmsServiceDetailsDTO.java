package pl.simpay.api.model.sms.details;

import com.squareup.moshi.Json;
import pl.simpay.api.model.sms.ServiceStatus;
import pl.simpay.api.model.sms.SmsServiceType;

import java.time.LocalDateTime;
import java.util.List;

public record SmsServiceDetailsDTO(
        int id,
        SmsServiceType type,
        ServiceStatus status,
        String name,
        String prefix,
        String suffix,
        boolean adult,
        List<String> numbers,
        @Json(name = "created_at") LocalDateTime createdAt
) {
}
