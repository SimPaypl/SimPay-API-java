package pl.simpay.api.model.generic;

import lombok.Data;

import java.util.List;

@Data
public class APIResponse<T> {
    private T respond;
    private List<APIError> error;
}
