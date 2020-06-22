package pl.simpay.api.model.db.requests;

import lombok.Data;

@Data
public class DbServiceCommissionRequest {
    private String api;
    private String secret;
    private String service_id;
}
