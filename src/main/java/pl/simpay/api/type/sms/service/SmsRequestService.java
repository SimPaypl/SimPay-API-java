package pl.simpay.api.type.sms.service;

import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;
import pl.simpay.api.type.ParametersWrapper;
import pl.simpay.api.type.sms.request.StatusParameters;
import pl.simpay.api.type.sms.response.domain.SmsStatusResponse;
import pl.simpay.api.util.GsonUtil;
import pl.simpay.api.util.HttpPostService;

import java.io.IOException;

import static pl.simpay.config.ApiParameters.SMS_API_URL;

@Service
public class SmsRequestService {

    private HttpPostService httpPostService;

    public SmsRequestService(HttpPostService httpPostService) {
        this.httpPostService = httpPostService;
    }

    public SmsStatusResponse getResponse(String serviceId, String number, String code) throws IOException {
        ParametersWrapper parameters = new ParametersWrapper(new StatusParameters(serviceId, number, code));
        HttpResponse response = httpPostService.getResponseWithEntityAsJson(SMS_API_URL, GsonUtil.toJson(parameters));

        return GsonUtil.convertToSmsStatusResponse(response.getEntity().getContent());
    }
}
