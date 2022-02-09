package pl.simpay.api.model.response;

import java.util.List;
import java.util.Map;

public record Response<T>(boolean success, T data, Map<String, List<String>> errors) {
}
