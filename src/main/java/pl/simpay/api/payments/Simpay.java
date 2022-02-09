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

    public Simpay(String apiKey, String apiPassword) {
        var restService = new RestService(apiKey, apiPassword);
        this.sms = new Sms(restService);
        this.directBilling = new DirectBilling(restService);
    }

    public SmsXml getSmsXml(String hashingKey) {
        return Optional.ofNullable(smsXml).orElseGet(() -> {
            this.smsXml = new SmsXml(hashingKey);
            return smsXml;
        });
    }
}
