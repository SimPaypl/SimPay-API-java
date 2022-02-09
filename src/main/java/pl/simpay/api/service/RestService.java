package pl.simpay.api.service;

import com.squareup.moshi.Moshi;
import dev.zacsweers.moshix.records.RecordsJsonAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import pl.simpay.api.adapter.*;
import pl.simpay.api.exception.ApiException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
public class RestService {
    private static final String BASE_URL = "https://api.simpay.pl";
    private static final String APPLICATION_JSON_MIME_TYPE = "application/json";

    private final Moshi moshi;
    private final OkHttpClient httpClient;

    public RestService(String simKey, String simPassword) {
        this.moshi = buildMoshiUnmarshaller(new DirectBillingServiceStatusTypeAdapter(), new SmsServiceStatusAdapter(), new OperatorAdapter(), new TransactionStatusAdapter(), new DateTimeAdapter());
        this.httpClient = new OkHttpClient.Builder().addInterceptor(new AuthInterceptor(simKey, simPassword)).build();
    }

    private Moshi buildMoshiUnmarshaller(Adapter<?>... adapters) {
        var builder = new Moshi.Builder();
        builder.add(new RecordsJsonAdapterFactory());
        Arrays.stream(adapters).forEach(builder::add);
        return builder.build();
    }

    public Object sendGetRequest(String endpoint, ParameterizedType responseType) {
        log.debug("Get Request To Endpoint: {}", endpoint);
        var request = new Request.Builder().url(BASE_URL + endpoint).get().build();
        try {
            return moshi.adapter(responseType).fromJson(executeRequest(request));
        } catch (IOException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public <REQ> Object sendPostRequest(String endpoint, REQ requestBody, Class<REQ> requestType, ParameterizedType responseType) {
        var body = moshi.adapter(requestType).toJson(requestBody);
        log.debug("Post Request To Endpoint {}, RequestBody: {}", endpoint, body);
        var request = new Request.Builder().url(BASE_URL + endpoint).post(RequestBody.create(body, MediaType.parse(APPLICATION_JSON_MIME_TYPE))).build();
        try {
            return moshi.adapter(responseType).fromJson(executeRequest(request));
        } catch (IOException e) {
            throw new ApiException(e.getMessage());
        }
    }

    private String executeRequest(Request request) {
        try (var response = httpClient.newCall(request).execute()) {
            var body = Objects.requireNonNull(response.body()).string();
            log.debug("Api Response: {}", body);
            return body;
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }
}
