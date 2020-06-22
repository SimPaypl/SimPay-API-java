package pl.simpay.api.model.sms.request;

import lombok.Data;

@Data
public class CodeVerifyRequest {
    private String key;
    private String secret;
    private String service_id;
    private String number;
    private String code;
}
