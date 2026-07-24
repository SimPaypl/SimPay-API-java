package pl.simpay.api.service;

import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;
import dev.zacsweers.moshix.records.RecordsJsonAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import pl.simpay.api.adapter.Adapter;
import pl.simpay.api.adapter.DateTimeAdapter;
import pl.simpay.api.adapter.DirectBillingServiceStatusTypeAdapter;
import pl.simpay.api.adapter.OperatorAdapter;
import pl.simpay.api.adapter.SmsServiceStatusAdapter;
import pl.simpay.api.adapter.TransactionStatusAdapter;
import pl.simpay.api.adapter.JavaTimeAdapter;
import pl.simpay.api.adapter.BigDecimalJsonAdapter;
import pl.simpay.api.exception.SimPayApiException;
import pl.simpay.api.exception.SimPayNetworkException;
import pl.simpay.api.exception.SimPaySerializationException;
import pl.simpay.api.exception.SimPayTimeoutException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.math.BigDecimal;

@Slf4j
public class RestService {
    private static final HttpUrl BASE_URL = Objects.requireNonNull(HttpUrl.parse("https://api.simpay.pl"));
    private static final MediaType APPLICATION_JSON = Objects.requireNonNull(MediaType.parse("application/json"));

    private final Moshi moshi;
    private final OkHttpClient httpClient;
    private final HttpUrl baseUrl;

    public RestService(String simKey, String simPassword) {
        this(simKey, simPassword, BASE_URL, new OkHttpClient.Builder());
    }

    RestService(String simKey, String simPassword, HttpUrl baseUrl, OkHttpClient.Builder httpClientBuilder) {
        this.moshi = buildMoshiUnmarshaller(new DirectBillingServiceStatusTypeAdapter(), new SmsServiceStatusAdapter(), new OperatorAdapter(), new TransactionStatusAdapter(), new DateTimeAdapter());
        this.baseUrl = baseUrl;
        this.httpClient = httpClientBuilder.addInterceptor(new AuthInterceptor(simKey, simPassword)).build();
    }

    public static RestService bearer(String token) {
        return bearer(token, BASE_URL, new OkHttpClient.Builder());
    }

    static RestService bearer(String token, HttpUrl baseUrl, OkHttpClient.Builder builder) {
        return new RestService(baseUrl, builder.addInterceptor(new BearerAuthInterceptor(token)));
    }

    private RestService(HttpUrl baseUrl, OkHttpClient.Builder builder) {
        this.moshi = buildMoshiUnmarshaller(new DirectBillingServiceStatusTypeAdapter(), new SmsServiceStatusAdapter(), new OperatorAdapter(), new TransactionStatusAdapter(), new DateTimeAdapter());
        this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl");
        this.httpClient = builder.build();
    }

    private Moshi buildMoshiUnmarshaller(Adapter<?>... adapters) {
        var builder = new Moshi.Builder().add(BigDecimal.class, new BigDecimalJsonAdapter()).add(new JavaTimeAdapter());
        builder.add(new RecordsJsonAdapterFactory());
        Arrays.stream(adapters).forEach(builder::add);
        return builder.build();
    }

    public Object sendGetRequest(String endpoint, ParameterizedType responseType) {
        return sendGetRequest(endpoint, Map.of(), responseType);
    }

    public Object sendGetRequest(String endpoint, Type responseType) {
        return sendGetRequest(endpoint, Map.of(), responseType);
    }

    public Object sendGetRequest(String endpoint, Map<String, String> queryParameters, ParameterizedType responseType) {
        return sendGetRequest(endpoint, queryParameters, (Type) responseType);
    }

    public Object sendGetRequest(String endpoint, Map<String, String> queryParameters, Type responseType) {
        var urlBuilder = resolveEndpoint(endpoint).newBuilder();
        queryParameters.forEach(urlBuilder::addQueryParameter);
        var request = new Request.Builder().url(urlBuilder.build()).get().build();
        try {
            return deserialize(executeRequest(request), responseType);
        } catch (IOException | JsonDataException e) {
            throw new SimPaySerializationException("Could not deserialize SimPay API response", e);
        }
    }

    public <REQ> Object sendPostRequest(String endpoint, REQ requestBody, Class<REQ> requestType, ParameterizedType responseType) {
        return sendJsonRequest("POST", endpoint, requestBody, requestType, responseType);
    }

    public <REQ> Object sendPostRequest(String endpoint, REQ requestBody, Class<REQ> requestType, Type responseType) {
        return sendJsonRequest("POST", endpoint, requestBody, requestType, responseType);
    }

    public <REQ> Object sendDeleteRequest(String endpoint, REQ requestBody, Class<REQ> requestType, Type responseType) {
        return sendJsonRequest("DELETE", endpoint, requestBody, requestType, responseType);
    }

    private <REQ> Object sendJsonRequest(String method, String endpoint, REQ requestBody, Class<REQ> requestType, Type responseType) {
        final String body;
        try {
            body = moshi.adapter(requestType).toJson(requestBody);
        } catch (JsonDataException | IllegalArgumentException e) {
            throw new SimPaySerializationException("Could not serialize SimPay API request", e);
        }

        var request = new Request.Builder()
                .url(resolveEndpoint(endpoint))
                .method(method, RequestBody.create(body, APPLICATION_JSON))
                .build();
        try {
            return deserialize(executeRequest(request), responseType);
        } catch (IOException | JsonDataException e) {
            throw new SimPaySerializationException("Could not deserialize SimPay API response", e);
        }
    }

    private HttpResult executeRequest(Request request) {
        var startedAt = System.nanoTime();
        try (var response = httpClient.newCall(request).execute()) {
            var body = response.body() == null ? "" : response.body().string();
            var durationMs = (System.nanoTime() - startedAt) / 1_000_000;
            log.debug("{} {} -> {} ({} ms)", request.method(), request.url().encodedPath(), response.code(), durationMs);

            if (!response.isSuccessful()) {
                throw apiException(response.code(), body);
            }
            return new HttpResult(response.code(), body);
        } catch (InterruptedIOException e) {
            throw new SimPayTimeoutException("SimPay API request timed out", e);
        } catch (IOException e) {
            throw new SimPayNetworkException("SimPay API network request failed", e);
        }
    }

    private Object deserialize(HttpResult result, Type responseType) throws IOException {
        if (result.statusCode() == 204) return null;
        return moshi.adapter(responseType).fromJson(result.body());
    }

    private HttpUrl resolveEndpoint(String endpoint) {
        var url = baseUrl.resolve(endpoint);
        if (url == null) {
            throw new IllegalArgumentException("Invalid SimPay API endpoint: " + endpoint);
        }
        return url;
    }

    private SimPayApiException apiException(int statusCode, String responseBody) {
        String message = "SimPay API request failed with status " + statusCode;
        String errorCode = null;

        Map<String, List<String>> errors = Map.of();
        try {
            var parsedBody = JsonReader.of(new Buffer().writeUtf8(responseBody)).readJsonValue();
            if (parsedBody instanceof Map<?, ?> error) {
                var apiMessage = error.get("message");
                var apiErrorCode = error.containsKey("errorCode") ? error.get("errorCode") : error.get("code");
                if (apiMessage instanceof String value && !value.isBlank()) message = value;
                if (apiErrorCode instanceof String value) errorCode = value;
                if (error.get("errors") instanceof Map<?, ?> rawErrors) {
                    var parsed = new java.util.LinkedHashMap<String, List<String>>();
                    rawErrors.forEach((key, value) -> {
                        var messages = new ArrayList<String>();
                        if (value instanceof List<?> values) values.forEach(item -> messages.add(String.valueOf(item)));
                        else if (value != null) messages.add(String.valueOf(value));
                        parsed.put(String.valueOf(key), List.copyOf(messages));
                    });
                    errors = Map.copyOf(parsed);
                }
            }
        } catch (Exception ignored) {
            // Preserve the raw body on the exception when the API returns non-JSON content.
        }

        if ((statusCode == 400) && errorCode != null && java.util.Set.of("INVALID_BLIK_CODE", "INVALID_BLIK_CODE_FORMAT", "BLIK_CODE_EXPIRED", "BLIK_CODE_CANCELLED", "BLIK_CODE_USED", "BLIK_CODE_NOT_SUPPORTED").contains(errorCode))
            return new pl.simpay.api.exception.InvalidBlikTicketException(message, statusCode, errorCode, responseBody);
        if (statusCode == 401 || statusCode == 403) return new pl.simpay.api.exception.SimPayAuthorizationException(message, statusCode, errorCode, responseBody);
        if (statusCode == 404) return new pl.simpay.api.exception.SimPayNotFoundException(message, errorCode, responseBody);
        if (statusCode == 422) return new pl.simpay.api.exception.SimPayValidationException(message, errorCode, responseBody, errors);
        return new SimPayApiException(message, statusCode, errorCode, responseBody);
    }

    private record HttpResult(int statusCode, String body) {
    }
}
