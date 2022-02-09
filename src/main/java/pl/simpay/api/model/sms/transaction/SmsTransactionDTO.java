package pl.simpay.api.model.sms.transaction;

import com.squareup.moshi.Json;

import java.time.LocalDateTime;

public record SmsTransactionDTO(int id,
                                long from,
                                String code,
                                boolean used,
                                @Json(name = "send_at") LocalDateTime sendAt) {
}
