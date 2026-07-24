package pl.simpay.api.service;

import com.squareup.moshi.Types;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.simpay.api.exception.SimPayApiException;
import pl.simpay.api.exception.SimPayNetworkException;
import pl.simpay.api.exception.SimPaySerializationException;
import pl.simpay.api.exception.SimPayTimeoutException;
import pl.simpay.api.model.sms.SmsServiceDTO;
import pl.simpay.api.model.response.Response;
import pl.simpay.api.payments.DirectBilling;

import java.io.IOException;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestServiceTest {

    private MockWebServer server;
    private RestService restService;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        restService = new RestService("api-key", "api-password", server.url("/"), new OkHttpClient.Builder());
    }

    @AfterEach
    void tearDown() throws IOException {
        if (server != null) server.shutdown();
    }

    @Test
    void deserializesSuccessfulJsonResponse() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\":true,\"data\":\"ok\",\"errors\":{}}"));

        var responseType = Types.newParameterizedType(Response.class, String.class);
        var response = (Response<?>) restService.sendGetRequest("/test", responseType);

        assertTrue(response.success());
        assertEquals("ok", response.data());
    }

    @Test
    void exposesStatusAndApiErrorForUnsuccessfulResponse() {
        server.enqueue(new MockResponse()
                .setResponseCode(422)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\":false,\"errorCode\":\"VALIDATION_ERROR\",\"errors\":{\"amount\":[\"Required\"]}}"));

        var exception = assertThrows(
                SimPayApiException.class,
                () -> restService.sendGetRequest("/test", Types.newParameterizedType(Response.class, String.class))
        );

        assertEquals(422, exception.getStatusCode());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("status 422"));
    }

    @Test
    void supportsSuccessfulNoContentResponse() {
        server.enqueue(new MockResponse().setResponseCode(204));

        var response = restService.sendGetRequest("/test", Types.newParameterizedType(Response.class, String.class));

        assertNull(response);
    }

    @Test
    void treatsBlankOkResponseAsSerializationFailure() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(""));

        assertThrows(
                SimPaySerializationException.class,
                () -> restService.sendGetRequest("/test", Types.newParameterizedType(Response.class, String.class))
        );
    }

    @Test
    void distinguishesMalformedJsonFromApiAndNetworkErrors() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{invalid-json"));

        assertThrows(
                SimPaySerializationException.class,
                () -> restService.sendGetRequest("/test", Types.newParameterizedType(Response.class, String.class))
        );
    }

    @Test
    void distinguishesTimeoutFromOtherNetworkErrors() {
        restService = new RestService(
                "api-key",
                "api-password",
                server.url("/"),
                new OkHttpClient.Builder().callTimeout(Duration.ofMillis(100))
        );
        server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));

        assertThrows(
                SimPayTimeoutException.class,
                () -> restService.sendGetRequest("/test", Types.newParameterizedType(Response.class, String.class))
        );
    }

    @Test
    void distinguishesOtherNetworkErrorsFromTimeouts() throws IOException {
        var unavailableUrl = server.url("/");
        server.shutdown();
        server = null;
        restService = new RestService("api-key", "api-password", unavailableUrl, new OkHttpClient.Builder());

        var exception = assertThrows(
                SimPayNetworkException.class,
                () -> restService.sendGetRequest("/test", Types.newParameterizedType(Response.class, String.class))
        );

        assertTrue(!(exception instanceof SimPayTimeoutException));
    }

    @Test
    void encodesQueryParametersWithoutDependingOnDefaultLocale() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(204));
        var previousLocale = Locale.getDefault();

        try {
            Locale.setDefault(Locale.forLanguageTag("pl-PL"));
            new DirectBilling(restService).calculateCommission("d151e4f9", 10.5);
        } finally {
            Locale.setDefault(previousLocale);
        }

        assertEquals("10.5", server.takeRequest().getRequestUrl().queryParameter("amount"));
    }


    @Test
    void deserializesCurrentHexadecimalServiceId() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"success\":true,\"data\":{" +
                        "\"id\":\"d151e4f9\"," +
                        "\"type\":\"ONE_TIME_CODE\"," +
                        "\"status\":\"service_active\"," +
                        "\"name\":\"Test\"," +
                        "\"prefix\":\"TEST\"," +
                        "\"suffix\":\"\"," +
                        "\"adult\":false," +
                        "\"created_at\":\"2026-07-24T12:30:00+02:00\"" +
                        "},\"errors\":{}}"));

        var responseType = Types.newParameterizedType(Response.class, SmsServiceDTO.class);
        var response = (Response<?>) restService.sendGetRequest("/sms/d151e4f9", responseType);
        var service = (SmsServiceDTO) response.data();

        assertEquals("d151e4f9", service.id());
    }
}
