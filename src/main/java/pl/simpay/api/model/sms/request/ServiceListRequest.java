package pl.simpay.api.model.sms.request;

import lombok.Data;

@Data
public class ServiceListRequest {
    private String key;
    private String secret;
}
