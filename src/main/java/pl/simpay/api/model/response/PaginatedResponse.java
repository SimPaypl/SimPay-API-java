package pl.simpay.api.model.response;

import pl.simpay.api.model.pagination.Pagination;

import java.util.List;
import java.util.Map;

public record PaginatedResponse<T>(boolean success, T data, Pagination pagination, Map<String, List<String>> errors) {
}
