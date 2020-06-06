package pl.simpay.api.type.db.controller;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.simpay.api.type.db.domain.DbPaymentResponse;
import pl.simpay.api.type.db.service.PaymentResponseBuilder;
import pl.simpay.api.type.db.type.PaymentResponseStatus;

import static pl.simpay.api.util.Sha256Util.dbPaymentResponseSignIsValid;

@RestController
public class DbResponseController {

    private static final String CONFIRMATION_RESPONSE = "OK";

    @PostMapping(value = "/simpay_api")
    public String getResponse(@RequestParam MultiValueMap<String, String> responseData) {
        DbPaymentResponse dbPaymentResponse = PaymentResponseBuilder.buildPaymentResponse(responseData);

        if (dbPaymentResponseSignIsValid(dbPaymentResponse)
                && PaymentResponseStatus.ORDER_PAYED.equals(dbPaymentResponse.getStatus())) {

            System.out.println("Payment has been successfully completed");
        }

        if (dbPaymentResponseSignIsValid(dbPaymentResponse)
                && PaymentResponseStatus.ORDER_ACCEPTED.equals(dbPaymentResponse.getStatus())) {

            System.out.println("Order has been accepted - but not paid yet");
        }

        if (dbPaymentResponseSignIsValid(dbPaymentResponse)
                && PaymentResponseStatus.ORDER_REJECTED.equals(dbPaymentResponse.getStatus())) {

            System.out.println("Payment has been rejected");
        }

        return CONFIRMATION_RESPONSE;
    }
}
