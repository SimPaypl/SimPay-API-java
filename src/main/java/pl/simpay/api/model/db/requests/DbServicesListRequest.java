package pl.simpay.api.model.db.requests;

import lombok.Data;

@Data
public class DbServicesListRequest {
    private String key;
    private String secret;
}
