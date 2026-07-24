package pl.simpay.api.payments;

import com.squareup.moshi.Types;
import lombok.RequiredArgsConstructor;
import pl.simpay.api.model.sms.code.CodeVerifyDTO;
import pl.simpay.api.model.sms.number.NumberDTO;
import pl.simpay.api.model.sms.SmsServiceDTO;
import pl.simpay.api.model.sms.details.SmsServiceDetailsDTO;
import pl.simpay.api.model.sms.transaction.SmsTransactionDTO;
import pl.simpay.api.model.sms.transaction.SmsTransactionDetailsDTO;
import pl.simpay.api.model.request.CodeVerifyRequest;
import pl.simpay.api.model.response.PaginatedResponse;
import pl.simpay.api.model.response.Response;
import pl.simpay.api.service.RestService;

import java.util.Set;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class Sms {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_LIMIT = 15;

    private final RestService restService;

    public PaginatedResponse<Set<SmsServiceDTO>> getServiceList(){
        return getServiceList(DEFAULT_PAGE, DEFAULT_LIMIT);
    }

    public PaginatedResponse<Set<SmsServiceDTO>> getServiceList(int page, int limit) {
        var endpoint = String.format("/sms?page=%d&limit=%d", page, limit);
        var parameterizedType = Types.newParameterizedType(PaginatedResponse.class, Types.newParameterizedType(Set.class, SmsServiceDTO.class));
        return (PaginatedResponse<Set<SmsServiceDTO>>) restService.sendGetRequest(endpoint, parameterizedType);
    }

    public Response<SmsServiceDetailsDTO> getServiceDetails(String serviceId) {
        var endpoint = String.format("/sms/%s", serviceId);
        var parameterizedType = Types.newParameterizedType(Response.class, SmsServiceDetailsDTO.class);
        return (Response<SmsServiceDetailsDTO>) restService.sendGetRequest(endpoint, parameterizedType);
    }

    public PaginatedResponse<Set<SmsTransactionDTO>> getTransactions(String serviceId) {
        return getTransactions(serviceId, DEFAULT_PAGE, DEFAULT_LIMIT);
    }

    public PaginatedResponse<Set<SmsTransactionDTO>> getTransactions(String serviceId, int page, int limit) {
        var endpoint = String.format("/sms/%s/transactions?page=%d&limit=%d", serviceId, page, limit);
        var parameterizedType = Types.newParameterizedType(PaginatedResponse.class, Types.newParameterizedType(Set.class, SmsTransactionDTO.class));
        return (PaginatedResponse<Set<SmsTransactionDTO>>) restService.sendGetRequest(endpoint, parameterizedType);
    }

    public Response<SmsTransactionDetailsDTO> getTransactionDetails(String serviceId, int transactionId) {
        var endpoint = String.format("/sms/%s/transactions/%d", serviceId, transactionId);
        var parameterizedType = Types.newParameterizedType(Response.class, SmsTransactionDetailsDTO.class);
        return (Response<SmsTransactionDetailsDTO>) restService.sendGetRequest(endpoint, parameterizedType);
    }

    public PaginatedResponse<Set<NumberDTO>> getServiceNumbers(String serviceId){
        return getServiceNumbers(serviceId, DEFAULT_PAGE, DEFAULT_LIMIT);
    }

    public PaginatedResponse<Set<NumberDTO>> getServiceNumbers(String serviceId, int page, int limit) {
        var endpoint = String.format("/sms/%s/numbers?page=%d&limit=%d", serviceId, page, limit);
        var parameterizedType = Types.newParameterizedType(PaginatedResponse.class, Types.newParameterizedType(Set.class, NumberDTO.class));
        return (PaginatedResponse<Set<NumberDTO>>) restService.sendGetRequest(endpoint, parameterizedType);
    }

    public Response<NumberDTO> getServiceNumbersDetails(String serviceId, long number) {
        var endpoint = String.format("/sms/%s/numbers/%d", serviceId, number);
        var parameterizedType = Types.newParameterizedType(Response.class, NumberDTO.class);
        return (Response<NumberDTO>) restService.sendGetRequest(endpoint, parameterizedType);
    }

    public PaginatedResponse<Set<NumberDTO>> getNumbers(){
        return getNumbers(DEFAULT_PAGE, DEFAULT_LIMIT);
    }

    public PaginatedResponse<Set<NumberDTO>> getNumbers(int page, int limit) {
        var endpoint = String.format("/sms/numbers?page=%d&limit=%d", page, limit);
        var parameterizedType = Types.newParameterizedType(PaginatedResponse.class, Types.newParameterizedType(Set.class, NumberDTO.class));
        return (PaginatedResponse<Set<NumberDTO>>) restService.sendGetRequest(endpoint, parameterizedType);
    }

    public Response<NumberDTO> getNumberDetails(long number) {
        var endpoint = String.format("/sms/numbers/%d", number);
        var parameterizedType = Types.newParameterizedType(Response.class, NumberDTO.class);
        return (Response<NumberDTO>) restService.sendGetRequest(endpoint, parameterizedType);
    }

    public Response<CodeVerifyDTO> verifyCode(String serviceId, String code, long number) {
        var endpoint = String.format("/sms/%s", serviceId);
        var parameterizedType = Types.newParameterizedType(Response.class, CodeVerifyDTO.class);
        return (Response<CodeVerifyDTO>) restService.sendPostRequest(endpoint, new CodeVerifyRequest(code, number), CodeVerifyRequest.class, parameterizedType);
    }
}
