package pl.simpay.api.model.directBilling.commision;

import com.squareup.moshi.Json;

public record CommissionsValuesDTO(CommissionValue orange, CommissionValue play,
                                   @Json(name = "t-mobile") CommissionValue tMobile, CommissionValue plus) {
}
