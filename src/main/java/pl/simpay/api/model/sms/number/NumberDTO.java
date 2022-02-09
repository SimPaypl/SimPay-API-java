package pl.simpay.api.model.sms.number;

import com.squareup.moshi.Json;

public record NumberDTO(
        long number,
        double value,
        @Json(name = "value_gross") double valueGross,
        boolean adult
) {
}
