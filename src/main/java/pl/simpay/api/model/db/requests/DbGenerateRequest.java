package pl.simpay.api.model.db.requests;

import lombok.Data;
import pl.simpay.api.model.generic.Operator;

@Data
public class DbGenerateRequest {
    private String serviceId;
    private String control;
    private String complete;
    private String failure;
    private String amount;
    private String amount_gross;
    private String amount_required;
    private Operator provider;
    private String sign;
}
