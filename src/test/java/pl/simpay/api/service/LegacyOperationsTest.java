package pl.simpay.api.service;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.simpay.api.model.request.GenerateTransactionRequest;
import pl.simpay.api.payments.DirectBilling;
import pl.simpay.api.payments.Sms;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LegacyOperationsTest {
    private MockWebServer server;
    private Sms sms;
    private DirectBilling directBilling;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        var rest = new RestService("api-key", "api-password", server.url("/"), new OkHttpClient.Builder());
        sms = new Sms(rest);
        directBilling = new DirectBilling(rest);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    private RecordedRequest request() throws InterruptedException {
        var request = server.takeRequest();
        assertEquals("api-key", request.getHeader("X-SIM-KEY"));
        assertEquals("api-password", request.getHeader("X-SIM-PASSWORD"));
        assertEquals("3.0.0", request.getHeader("X-SIM-VERSION"));
        assertEquals("JAVA", request.getHeader("X-SIM-PLATFORM"));
        return request;
    }

    private void noContent() {
        server.enqueue(new MockResponse().setResponseCode(204));
    }

    private void assertGet(String expectedPath) throws InterruptedException {
        var request = request();
        assertEquals("GET", request.getMethod());
        assertEquals(expectedPath, request.getPath());
    }

    @Test void smsDefaultServiceList() throws Exception {
        noContent(); sms.getServiceList();
        assertGet("/sms?page=1&limit=15");
    }

    @Test void smsConfiguredServiceList() throws Exception {
        noContent(); sms.getServiceList(2, 30);
        assertGet("/sms?page=2&limit=30");
    }

    @Test void smsServiceDetails() throws Exception {
        noContent(); sms.getServiceDetails("e14f8074");
        assertGet("/sms/e14f8074");
    }

    @Test void smsDefaultTransactions() throws Exception {
        noContent(); sms.getTransactions("e14f8074");
        assertGet("/sms/e14f8074/transactions?page=1&limit=15");
    }

    @Test void smsConfiguredTransactions() throws Exception {
        noContent(); sms.getTransactions("e14f8074", 3, 40);
        assertGet("/sms/e14f8074/transactions?page=3&limit=40");
    }

    @Test void smsTransactionDetails() throws Exception {
        noContent(); sms.getTransactionDetails("e14f8074", 123);
        assertGet("/sms/e14f8074/transactions/123");
    }

    @Test void smsDefaultServiceNumbers() throws Exception {
        noContent(); sms.getServiceNumbers("e14f8074");
        assertGet("/sms/e14f8074/numbers?page=1&limit=15");
    }

    @Test void smsConfiguredServiceNumbers() throws Exception {
        noContent(); sms.getServiceNumbers("e14f8074", 4, 50);
        assertGet("/sms/e14f8074/numbers?page=4&limit=50");
    }

    @Test void smsServiceNumberDetails() throws Exception {
        noContent(); sms.getServiceNumbersDetails("e14f8074", 7055);
        assertGet("/sms/e14f8074/numbers/7055");
    }

    @Test void smsDefaultGlobalNumbers() throws Exception {
        noContent(); sms.getNumbers();
        assertGet("/sms/numbers?page=1&limit=15");
    }

    @Test void smsConfiguredGlobalNumbers() throws Exception {
        noContent(); sms.getNumbers(5, 60);
        assertGet("/sms/numbers?page=5&limit=60");
    }

    @Test void smsGlobalNumberDetails() throws Exception {
        noContent(); sms.getNumberDetails(7055);
        assertGet("/sms/numbers/7055");
    }

    @Test void smsVerifyCodeWithNumber() throws Exception {
        noContent(); sms.verifyCode("e14f8074", "ABC", 7055);
        var request = request();
        assertEquals("POST", request.getMethod());
        assertEquals("/sms/e14f8074", request.getPath());
        assertEquals("{\"code\":\"ABC\",\"number\":7055}", request.getBody().readUtf8());
    }

    @Test void smsVerifyCodeWithoutNumber() throws Exception {
        noContent(); sms.verifyCode("e14f8074", "ABC");
        var request = request();
        assertEquals("POST", request.getMethod());
        assertEquals("/sms/e14f8074", request.getPath());
        assertEquals("{\"code\":\"ABC\"}", request.getBody().readUtf8());
    }

    @Test void directBillingDefaultServiceList() throws Exception {
        noContent(); directBilling.getServiceList();
        assertGet("/directbilling?page=1&limit=15");
    }

    @Test void directBillingConfiguredServiceList() throws Exception {
        noContent(); directBilling.getServiceList(2, 30);
        assertGet("/directbilling?page=2&limit=30");
    }

    @Test void directBillingServiceDetails() throws Exception {
        noContent(); directBilling.getServiceDetails("d151e4f9");
        assertGet("/directbilling/d151e4f9");
    }

    @Test void directBillingCommissionCalculation() throws Exception {
        noContent(); directBilling.calculateCommission("d151e4f9", 10.50);
        assertGet("/directbilling/d151e4f9/calculate?amount=10.5");
    }

    @Test void directBillingDefaultTransactions() throws Exception {
        noContent(); directBilling.getTransactions("d151e4f9");
        assertGet("/directbilling/d151e4f9/transactions?page=1&limit=15");
    }

    @Test void directBillingConfiguredTransactions() throws Exception {
        noContent(); directBilling.getTransactions("d151e4f9", 3, 40);
        assertGet("/directbilling/d151e4f9/transactions?page=3&limit=40");
    }

    @Test void directBillingTransactionDetails() throws Exception {
        noContent(); directBilling.getTransactionDetails("d151e4f9", "tx-1");
        assertGet("/directbilling/d151e4f9/transactions/tx-1");
    }

    @Test void directBillingGenerateTransaction() throws Exception {
        var body = GenerateTransactionRequest.builder().amount(10.5).control("order-1").steamId("steam").email("a@example.com").build();
        noContent(); directBilling.generateTransaction("d151e4f9", body);
        var request = request();
        assertEquals("POST", request.getMethod());
        assertEquals("/directbilling/d151e4f9/transactions", request.getPath());
        assertEquals("{\"amount\":10.5,\"amountType\":\"gross\",\"control\":\"order-1\",\"email\":\"a@example.com\",\"steamid\":\"steam\"}", request.getBody().readUtf8());
    }

    @Test void directBillingSignsFullLegacyRequest() throws Exception {
        var body = GenerateTransactionRequest.builder()
                .amount(10.5)
                .description("description")
                .control("control")
                .returns(new pl.simpay.api.model.request.RedirectURL("https://ok", "https://fail"))
                .phoneNumber("48123")
                .build();
        body.sign("key");
        noContent(); directBilling.generateTransaction("d151e4f9", body);

        assertEquals("d69125c8d9cc2b940848b347aab01993b8488a3535302e41adfd633884c974cd",
                signatureFrom(request().getBody().readUtf8()));
    }

    @Test void directBillingSignsLegacyRequestWithOptionalFieldsMissing() throws Exception {
        var body = GenerateTransactionRequest.builder().amount(10.5).build();
        body.sign("key");
        noContent(); directBilling.generateTransaction("d151e4f9", body);

        assertEquals("0936ce2d086614967cea9ba6c47bb7e74342382b33842219bef683b31b82cfc9",
                signatureFrom(request().getBody().readUtf8()));
    }

    @Test void directBillingSignsAmountAndControl() throws Exception {
        var body = GenerateTransactionRequest.builder().amount(10.5).control("order-1").build();
        body.signWithAmountAndControl("key");
        noContent(); directBilling.generateTransaction("d151e4f9", body);

        assertEquals("32b25838e0f440bc03e445793fed3feaa5174e20bf97231e0ede48de428c5819",
                signatureFrom(request().getBody().readUtf8()));
    }

    private static String signatureFrom(String json) {
        var marker = "\"signature\":\"";
        var start = json.indexOf(marker) + marker.length();
        return json.substring(start, json.indexOf('"', start));
    }
}
