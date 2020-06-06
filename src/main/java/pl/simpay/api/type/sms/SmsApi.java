package pl.simpay.api.type.sms;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pl.simpay.api.type.sms.response.SimpayStatusType;
import pl.simpay.api.type.sms.response.domain.Error;
import pl.simpay.api.type.sms.response.domain.SmsStatusResponse;
import pl.simpay.api.type.sms.service.SmsRequestService;

import java.io.IOException;

import static java.util.Objects.nonNull;

@Service
public class SmsApi {

    private SmsRequestService smsRequestService;

    public SmsApi(SmsRequestService smsRequestService) {
        this.smsRequestService = smsRequestService;
    }

    public void getSmsStatus() throws IOException {

        SmsStatusResponse response = smsRequestService.getResponse("123", "7136", "xxx");

        if (nonNull(response)
                && nonNull(response.getRespond())
                && nonNull(response.getRespond().getStatus())
                && SimpayStatusType.OK.name().equals(response.getRespond().getStatus())) {

            System.out.println("The code is correct");
        }

        if (nonNull(response)
                && nonNull(response.getRespond())
                && nonNull(response.getRespond().getStatus())
                && SimpayStatusType.USED.name().equals(response.getRespond().getStatus())) {

            System.out.println("The code has been used");
        }

        if (nonNull(response)
                && !CollectionUtils.isEmpty(response.getError())) {

            Error error = response.getError().get(0);

            System.out.println("Error code: " + error.getErrorCode());
            System.out.println("Error message: " + error.getErrorName());
            System.out.println("Error value: " + error.getErrorValue());
        }
    }

}
