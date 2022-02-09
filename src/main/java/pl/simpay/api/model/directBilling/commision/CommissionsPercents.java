package pl.simpay.api.model.directBilling.commision;

import com.squareup.moshi.Json;

public record CommissionsPercents(@Json(name = "t-mobile") CommissionPercent tMobile, CommissionPercent orange, CommissionPercent play,
                                  CommissionPercent plus) {
}
