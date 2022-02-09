package pl.simpay.api.model.directBilling.details;

import com.squareup.moshi.Json;

public record OperatorMaxValues(@Json(name = "t-mobile") String tMobile, String orange, String play, String plus) {
}
