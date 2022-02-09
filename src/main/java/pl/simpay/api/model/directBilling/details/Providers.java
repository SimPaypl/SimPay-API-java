package pl.simpay.api.model.directBilling.details;

import com.squareup.moshi.Json;

public record Providers(@Json(name = "t-mobile") boolean tMobile, boolean orange, boolean play, boolean plus) {
}
