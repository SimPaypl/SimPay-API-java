package pl.simpay.api.payments;

import com.google.common.hash.Hashing;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import dev.zacsweers.moshix.records.RecordsJsonAdapterFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import pl.simpay.api.adapter.TransactionStatusAdapter;
import pl.simpay.api.model.directBilling.DirectBillingServiceDTO;
import pl.simpay.api.model.directBilling.commision.CommissionsValuesDTO;
import pl.simpay.api.model.directBilling.details.DirectBillingServiceDetailsDTO;
import pl.simpay.api.model.directBilling.transaction.DirectBillingGenerateTransactionDTO;
import pl.simpay.api.model.directBilling.transaction.DirectBillingTransactionDetailsDTO;
import pl.simpay.api.model.directBilling.transaction.DirectBillingTransactionNotificationDTO;
import pl.simpay.api.model.directBilling.transaction.DirectBillingTransactionsDTO;
import pl.simpay.api.model.request.GenerateTransactionRequest;
import pl.simpay.api.model.response.PaginatedResponse;
import pl.simpay.api.model.response.Response;
import pl.simpay.api.service.RestService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class DirectBilling {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_LIMIT = 15;

    private final RestService restService;

    public PaginatedResponse<Set<DirectBillingServiceDTO>> getServiceList() {
        return getServiceList(DEFAULT_PAGE, DEFAULT_LIMIT);
    }

    public PaginatedResponse<Set<DirectBillingServiceDTO>> getServiceList(int page, int limit) {
        var endpoint = String.format("/directbilling?page=%d&limit=%d", page, limit);
        var parameterizedType = Types.newParameterizedType(PaginatedResponse.class, Types.newParameterizedType(Set.class, DirectBillingServiceDTO.class));
        return (PaginatedResponse<Set<DirectBillingServiceDTO>>) restService.sendGetRequest(endpoint, parameterizedType);
    }

    public Response<DirectBillingServiceDetailsDTO> getServiceDetails(int serviceId) {
        var endpoint = String.format("/directbilling/%d", serviceId);
        var parameterizedType = Types.newParameterizedType(Response.class, DirectBillingServiceDetailsDTO.class);
        return (Response<DirectBillingServiceDetailsDTO>) restService.sendGetRequest(endpoint, parameterizedType);
    }

    public Response<CommissionsValuesDTO> calculateCommission(int serviceId, double amount) {
        var endpoint = String.format("/directbilling/%d/calculate?amount=%f", serviceId, amount);
        var parameterizedType = Types.newParameterizedType(Response.class, CommissionsValuesDTO.class);
        return (Response<CommissionsValuesDTO>) restService.sendGetRequest(endpoint, parameterizedType);
    }

    public PaginatedResponse<Set<DirectBillingTransactionsDTO>> getTransactions(int serviceId) {
        return getTransactions(serviceId, DEFAULT_PAGE, DEFAULT_LIMIT);
    }

    public PaginatedResponse<Set<DirectBillingTransactionsDTO>> getTransactions(int serviceId, int page, int limit) {
        var endpoint = String.format("/directbilling/%d/transactions?page=%d&limit=%d", serviceId, page, limit);
        var parameterizedType = Types.newParameterizedType(PaginatedResponse.class, Types.newParameterizedType(Set.class, DirectBillingTransactionsDTO.class));
        return (PaginatedResponse<Set<DirectBillingTransactionsDTO>>) restService.sendGetRequest(endpoint, parameterizedType);
    }

    public Response<DirectBillingTransactionDetailsDTO> getTransactionDetails(int serviceId, int transactionId) {
        var endpoint = String.format("/directbilling/%d/transactions/%d", serviceId, transactionId);
        var parameterizedType = Types.newParameterizedType(Response.class, DirectBillingTransactionDetailsDTO.class);
        return (Response<DirectBillingTransactionDetailsDTO>) restService.sendGetRequest(endpoint, parameterizedType);
    }

    public Response<DirectBillingGenerateTransactionDTO> generateTransaction(int serviceId, GenerateTransactionRequest request) {
        var endpoint = String.format("/directbilling/%d/transactions", serviceId);
        var parameterizedType = Types.newParameterizedType(Response.class, DirectBillingGenerateTransactionDTO.class);
        return (Response<DirectBillingGenerateTransactionDTO>) restService.sendPostRequest(endpoint, request, GenerateTransactionRequest.class, parameterizedType);
    }

    @SneakyThrows
    public boolean checkSignature(String key, String transactionJson) {
        var moshi = new Moshi.Builder().add(new RecordsJsonAdapterFactory()).add(new TransactionStatusAdapter()).build();
        var transactionNotification = moshi.adapter(DirectBillingTransactionNotificationDTO.class).fromJson(transactionJson);
        return transactionNotification != null && generateSignature(key, transactionNotification).equals(transactionNotification.signature());
    }

    private String generateSignature(String key, DirectBillingTransactionNotificationDTO notification) {
        var pipe = "|";
        var fields = List.of(String.valueOf(notification.id()), notification.status().getStatusName(), String.valueOf(notification.values().net()), String.valueOf(notification.values().gross()), String.valueOf(notification.values().partner()), notification.returns().success(), notification.returns().failure(), notification.control(), notification.number(), String.valueOf(notification.provider()), notification.signature(), key);
        return Hashing.sha256().hashString(String.join(pipe, fields), StandardCharsets.UTF_8).toString();
    }

}
