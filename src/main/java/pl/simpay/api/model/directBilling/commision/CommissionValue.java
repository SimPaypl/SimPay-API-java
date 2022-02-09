package pl.simpay.api.model.directBilling.commision;

import com.squareup.moshi.Json;

public record CommissionValue(@Json(name = "net") double netValue, @Json(name = "gross") double grossValue) {
}
