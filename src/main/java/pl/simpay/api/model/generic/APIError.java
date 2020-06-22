package pl.simpay.api.model.generic;

import lombok.Data;

@Data
public class APIError {
    private String error_code;
    private String error_name;
    private String error_value;
}
