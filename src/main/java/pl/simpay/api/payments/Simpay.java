package pl.simpay.api.payments;

import lombok.Getter;
import pl.simpay.api.service.RestService;

import java.util.Optional;

public class Simpay {

    private SmsXml smsXml;

    @Getter
    private final Sms sms;
    @Getter
    private final DirectBilling directBilling;
    private final RestService restService;

    public Simpay(String apiKey, String apiPassword) {
        var restService = new RestService(apiKey, apiPassword);
        this.restService = restService;
        this.sms = new Sms(restService);
        this.directBilling = new DirectBilling(restService);
    }

    /** Creates a current API client authenticated with a bearer token. */
    public Simpay(String bearerToken) {
        this(RestService.bearer(bearerToken));
    }

    Simpay(RestService restService) {
        this.restService = restService;
        this.sms = new Sms(restService);
        this.directBilling = new DirectBilling(restService);
    }

    public Payment getPayment(String serviceId) { return getPayment(serviceId, null); }
    public Payment getPayment(String serviceId, String ipnHash) { return new Payment(restService, serviceId, ipnHash); }

    /** Unbound convenience entrypoint; bind a service before making calls. */
    public PaymentFactory getPayment() { return new PaymentFactory(restService); }

    public static final class PaymentFactory {
        private final RestService rest;
        private PaymentFactory(RestService rest) { this.rest = rest; }
        public Payment forService(String serviceId) { return new Payment(rest, serviceId, null); }
        public Payment forService(String serviceId, String ipnHash) { return new Payment(rest, serviceId, ipnHash); }
    }

    public SmsXml getSmsXml(String hashingKey) {
        return Optional.ofNullable(smsXml).orElseGet(() -> {
            this.smsXml = new SmsXml(hashingKey);
            return smsXml;
        });
    }
}
