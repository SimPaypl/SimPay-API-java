package pl.simpay.api.payments;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import dev.zacsweers.moshix.records.RecordsJsonAdapterFactory;
import lombok.RequiredArgsConstructor;
import pl.simpay.api.adapter.BigDecimalJsonAdapter;
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
import pl.simpay.api.util.Sha256;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Map;
import java.util.Objects;
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

    public Response<DirectBillingServiceDetailsDTO> getServiceDetails(String serviceId) {
        var endpoint = String.format("/directbilling/%s", serviceId);
        var parameterizedType = Types.newParameterizedType(Response.class, DirectBillingServiceDetailsDTO.class);
        return (Response<DirectBillingServiceDetailsDTO>) restService.sendGetRequest(endpoint, parameterizedType);
    }

    public Response<CommissionsValuesDTO> calculateCommission(String serviceId, double amount) {
        var endpoint = String.format("/directbilling/%s/calculate", serviceId);
        var parameterizedType = Types.newParameterizedType(Response.class, CommissionsValuesDTO.class);
        return (Response<CommissionsValuesDTO>) restService.sendGetRequest(
                endpoint,
                Map.of("amount", BigDecimal.valueOf(amount).toPlainString()),
                parameterizedType
        );
    }

    public PaginatedResponse<Set<DirectBillingTransactionsDTO>> getTransactions(String serviceId) {
        return getTransactions(serviceId, DEFAULT_PAGE, DEFAULT_LIMIT);
    }

    public PaginatedResponse<Set<DirectBillingTransactionsDTO>> getTransactions(String serviceId, int page, int limit) {
        var endpoint = String.format("/directbilling/%s/transactions?page=%d&limit=%d", serviceId, page, limit);
        var parameterizedType = Types.newParameterizedType(PaginatedResponse.class, Types.newParameterizedType(Set.class, DirectBillingTransactionsDTO.class));
        return (PaginatedResponse<Set<DirectBillingTransactionsDTO>>) restService.sendGetRequest(endpoint, parameterizedType);
    }

    public Response<DirectBillingTransactionDetailsDTO> getTransactionDetails(String serviceId, String transactionId) {
        var endpoint = String.format("/directbilling/%s/transactions/%s", serviceId, transactionId);
        var parameterizedType = Types.newParameterizedType(Response.class, DirectBillingTransactionDetailsDTO.class);
        return (Response<DirectBillingTransactionDetailsDTO>) restService.sendGetRequest(endpoint, parameterizedType);
    }

    public Response<DirectBillingGenerateTransactionDTO> generateTransaction(String serviceId, GenerateTransactionRequest request) {
        var endpoint = String.format("/directbilling/%s/transactions", serviceId);
        var parameterizedType = Types.newParameterizedType(Response.class, DirectBillingGenerateTransactionDTO.class);
        return (Response<DirectBillingGenerateTransactionDTO>) restService.sendPostRequest(endpoint, request, GenerateTransactionRequest.class, parameterizedType);
    }

    public boolean checkSignature(String key, String transactionJson) {
        try {
            var moshi = new Moshi.Builder()
                    .add(BigDecimal.class, new BigDecimalJsonAdapter())
                    .add(new RecordsJsonAdapterFactory())
                    .add(new TransactionStatusAdapter())
                    .build();
            var transactionNotification = moshi.adapter(DirectBillingTransactionNotificationDTO.class).fromJson(transactionJson);
            if (transactionNotification == null || transactionNotification.signature() == null || !transactionNotification.signature().matches("(?i)[0-9a-f]{64}")) return false;
            return MessageDigest.isEqual(HexFormat.of().parseHex(generateSignature(key, transactionNotification)), HexFormat.of().parseHex(transactionNotification.signature()));
        } catch (Exception ignored) {
            return false;
        }
    }

    private String generateSignature(String key, DirectBillingTransactionNotificationDTO notification) {
        var pipe = "|";
        var fields = Arrays.asList(
                notification.id(),
                notification.serviceId(),
                notification.status() == null ? null : notification.status().getStatusName(),
                notification.values() == null ? null : notification.values().net(),
                notification.values() == null ? null : notification.values().gross(),
                notification.values() == null ? null : notification.values().partner(),
                notification.returns() == null ? null : notification.returns().complete(),
                notification.returns() == null ? null : notification.returns().failure(),
                notification.control(),
                notification.numberFrom(),
                notification.provider(),
                key
        ).stream().filter(Objects::nonNull).map(DirectBilling::signatureValue).toList();
        return Sha256.hash(String.join(pipe, fields));
    }

    private static String signatureValue(Object value) {
        if (value == null || Boolean.FALSE.equals(value)) return "";
        if (Boolean.TRUE.equals(value)) return "1";
        if (value instanceof BigDecimal decimal) return decimal.toPlainString();
        return String.valueOf(value);
    }

}
