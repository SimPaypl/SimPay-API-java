package pl.simpay.api.model.pagination;

import com.squareup.moshi.Json;

public record Pagination(
        int total,
        int count,
        @Json(name = "per_page") int perPage,
        @Json(name = "current_page") int currentPage,
        @Json(name = "total_pages") int totalPages,
        Links links
) {
}
