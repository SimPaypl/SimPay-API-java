package pl.simpay.api.model.generic;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParametrizedRequest<T> {
    private T params;
}
