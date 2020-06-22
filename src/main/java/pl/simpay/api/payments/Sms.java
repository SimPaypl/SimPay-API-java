package pl.simpay.api.payments;

import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import pl.simpay.api.model.generic.APIResponse;
import pl.simpay.api.model.generic.ParametrizedRequest;
import pl.simpay.api.model.sms.request.ServiceListRequest;
import pl.simpay.api.model.sms.request.CodeVerifyRequest;
import pl.simpay.api.model.sms.respond.CodeVerifyResponse;
import pl.simpay.api.model.sms.respond.ServicesResponse;
import pl.simpay.api.utils.HttpService;

@Data
public class Sms {
    private static final HttpService service = new HttpService();
    private static final String VERIFY_CODE_URL = "https://simpay.pl/api/status";
    private static final String SERVICE_LIST_URL = "https://simpay.pl/api/get_services";

    private static final TypeToken<APIResponse<CodeVerifyResponse>> SERVICE_LIST_RESPONSE = new TypeToken<APIResponse<CodeVerifyResponse>>() {};
    private static final TypeToken<APIResponse<ServicesResponse>> VERIFY_CODE_RESPONSE = new TypeToken<APIResponse<ServicesResponse>>() {};

    private String apiKey;
    private String secret;
    private int serviceId;

    public Sms(String apiKey, String secret, int serviceId) {
        this.apiKey = apiKey;
        this.secret = secret;
        this.serviceId = serviceId;
    }

    public Sms(String apiKey, String secret) {
        this.apiKey = apiKey;
        this.secret = secret;
    }

    // https://docs.simpay.pl/#weryfikacja-kodu
    @SneakyThrows public APIResponse<CodeVerifyResponse> verifyCode(@NonNull CodeVerifyRequest request) {
        if (request.getKey() == null) request.setKey(apiKey);
        if (request.getSecret() == null) request.setSecret(secret);

        return service.sendPost(VERIFY_CODE_URL, new ParametrizedRequest<>(request), VERIFY_CODE_RESPONSE.getType());
    }

    // https://docs.simpay.pl/#pobieranie-listy-uslug
    @SneakyThrows public APIResponse<ServicesResponse> getServiceList(@NonNull ServiceListRequest request) {
        if (request.getKey() == null) request.setKey(apiKey);
        if (request.getSecret() == null) request.setSecret(secret);

        return service.sendPost(SERVICE_LIST_URL, new ParametrizedRequest<>(request), SERVICE_LIST_RESPONSE.getType());
    }
}
