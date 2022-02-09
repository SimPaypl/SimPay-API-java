package pl.simpay.api.model.directBilling.commision;

import com.squareup.moshi.Json;

public record CommissionPercent(@Json(name = "commission_0") String commission0,
                                @Json(name = "commission_9") String commission9,
                                @Json(name = "commission_25") String commission25) {
}
