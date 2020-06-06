package pl.simpay.api.type.db.domain.paymentinformation;

import static pl.simpay.config.ApiParameters.API_KEY;
import static pl.simpay.config.ApiParameters.API_SECRET;

public class PaymentStatusRequest {
    private String id;
    private String key;
    private String secret;

    public PaymentStatusRequest(String id) {
        this.id = id;
        this.key = API_KEY;
        this.secret = API_SECRET;
    }
}
