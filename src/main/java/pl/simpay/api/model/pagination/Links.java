package pl.simpay.api.model.pagination;

import com.squareup.moshi.Json;

public record Links(@Json(name = "next_page") String nextPage, @Json(name = "prev_page") String prevPage) {
}
