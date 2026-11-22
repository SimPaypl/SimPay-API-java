package pl.simpay.api.service;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.simpay.api.exception.InvalidBlikTicketException;
import pl.simpay.api.exception.SimPayAuthorizationException;
import pl.simpay.api.exception.SimPayNotFoundException;
import pl.simpay.api.exception.SimPayValidationException;
import pl.simpay.api.model.payment.PaymentModels;
import pl.simpay.api.payments.DirectBilling;
import pl.simpay.api.payments.Payment;
import pl.simpay.api.payments.Sms;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {
    MockWebServer server;
    Payment payment;

    @BeforeEach void setup() throws Exception { server = new MockWebServer(); server.start(); payment = new Payment(RestService.bearer("token", server.url("/"), new OkHttpClient.Builder()), "svc", "hash"); }
    @AfterEach void close() throws Exception { server.shutdown(); }
    private void json(String body) { server.enqueue(new MockResponse().setResponseCode(200).addHeader("Content-Type","application/json").setBody(body)); }
    private okhttp3.mockwebserver.RecordedRequest request() throws Exception { var r=server.takeRequest(); assertEquals("Bearer token",r.getHeader("Authorization")); assertEquals("application/json",r.getHeader("Accept")); assertEquals("JAVA",r.getHeader("X-SIM-PLATFORM")); assertEquals("3.0.0",r.getHeader("X-SIM-VERSION")); return r; }

    @Test void generateAndTransactionInfo() throws Exception {
        json("{\"success\":true,\"data\":{\"redirectUrl\":\"https://pay\",\"transactionId\":\"tx\"}}");
        assertEquals("tx", payment.generateTransaction(new PaymentModels.GenerateTransactionRequest(new BigDecimal("10.20"))).transactionId());
        var r=request(); assertEquals("POST",r.getMethod()); assertEquals("/payment/svc/transactions",r.getPath()); assertEquals("{\"amount\":10.20,\"currency\":\"PLN\"}",r.getBody().readUtf8());
        json("{\"success\":true,\"data\":{\"id\":\"tx\",\"status\":\"transaction_paid\",\"amount\":{\"value\":10.20,\"currency\":\"PLN\"},\"customer\":{\"country\":\"PL\"},\"payer_transaction_id\":\"payer\"}}");
        var transaction = payment.transactionInfo("tx");
        assertEquals(PaymentModels.TransactionStatus.PAID, transaction.status());
        assertEquals("PL", transaction.customer().country());
        var transactionRequest = request();
        assertEquals("GET", transactionRequest.getMethod());
        assertEquals("/payment/svc/transactions/tx", transactionRequest.getPath());
    }

    @Test void transactionInfoReadsFraudStatuses() throws Exception {
        json("{\"success\":true,\"data\":{\"id\":\"tx\",\"status\":\"transaction_fraud\"}}");
        assertEquals(PaymentModels.TransactionStatus.FRAUD, payment.transactionInfo("tx").status());
        json("{\"success\":true,\"data\":{\"id\":\"tx\",\"status\":\"transaction_fraud_possibility\"}}");
        assertEquals(PaymentModels.TransactionStatus.FRAUD_POSSIBILITY, payment.transactionInfo("tx").status());
    }
    @Test void channelsAndCurrencies() throws Exception {
        json("{\"success\":true,\"data\":[{\"id\":\"blik\",\"name\":\"BLIK\",\"type\":\"blik\",\"img\":\"x\",\"currencies\":[\"PLN\"],\"amount\":{\"min\":1,\"max\":100}}]}");
        assertEquals("blik",payment.channels().get(0).id()); assertEquals("/payment/svc/channels",request().getPath());
        json("{\"success\":true,\"data\":[{\"iso\":\"PLN\",\"pln_rate\":\"1.0\",\"updated_at\":\"2026-01-01T00:00:00Z\"}]}");
        assertEquals("PLN",payment.currencies().get(0).iso()); assertEquals("/payment/svc/currencies",request().getPath());
    }

    @Test void generateSerializesEveryLaravelPaymentOption() throws Exception {
        var customer = new PaymentModels.Customer("Jan", "jan@example.com", "127.0.0.1", "PL");
        var address = new PaymentModels.Address("Jan", "Kowalski", "Testowa", "1", "2", "Warszawa", "mazowieckie", "00-001", "PL", "Firma");
        var antiFraud = new PaymentModels.AntiFraud("agent", "steam", "player", "uuid", "system");
        var cart = List.of(new PaymentModels.CartItem("Produkt", 2, new BigDecimal("5.00"), "Producent", "Kategoria", "SKU"));
        var requestBody = new PaymentModels.GenerateTransactionRequest(
                new BigDecimal("10.00"), "EUR", "Opis", "order-1", customer, antiFraud,
                address, address, cart, new PaymentModels.Returns("https://ok", "https://fail"),
                "blik", List.of("blik", "card"), new PaymentModels.ChannelTypes(), "https://shop"
        );
        json("{\"success\":true,\"data\":{\"redirectUrl\":\"https://pay\",\"transactionId\":\"tx\"}}");

        payment.generateTransaction(requestBody);

        var recorded = request();
        assertEquals("POST", recorded.getMethod());
        assertEquals("/payment/svc/transactions", recorded.getPath());
        var body = recorded.getBody().readUtf8();
        for (var expected : List.of(
                "\"amount\":10.00", "\"currency\":\"EUR\"", "\"description\":\"Opis\"",
                "\"control\":\"order-1\"", "\"countryCode\":\"PL\"", "\"antifraud\"",
                "\"useragent\":\"agent\"", "\"steamid\":\"steam\"", "\"billing\"",
                "\"shipping\"", "\"cart\"", "\"returns\"", "\"directChannel\":\"blik\"",
                "\"channels\":[\"blik\",\"card\"]", "\"channelTypes\"", "\"referer\":\"https://shop\""
        )) assertTrue(body.contains(expected), expected);
    }
    @Test void blikAndAliasNoContent() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(204)); assertTrue(payment.initiateBlikLevel0("tx","123456"));
        var r=request(); assertEquals("POST",r.getMethod()); assertEquals("/payment/svc/blik/level0/tx",r.getPath()); assertEquals("{\"ticket\":{\"T6\":\"123456\"}}",r.getBody().readUtf8());
        server.enqueue(new MockResponse().setResponseCode(204)); assertTrue(payment.unregisterBlikAlias(new PaymentModels.UnregisterAliasRequest(null,"uuid",null,"done")));
        r=request(); assertEquals("DELETE",r.getMethod()); assertEquals("/payment/svc/blik/aliases", r.getPath()); assertEquals("{\"uuid\":\"uuid\",\"reason\":\"done\"}",r.getBody().readUtf8());
    }
    @Test void subscriptionOperationsAndQueries() throws Exception {
        json("{\"data\":[],\"pagination\":{\"total\":0,\"count\":0,\"per_page\":20,\"current_page\":1,\"total_pages\":0,\"links\":{}}}");
        payment.subscriptions(new PaymentModels.SubscriptionQuery(1,20,"u",PaymentModels.SubscriptionStatus.ACTIVE,PaymentModels.SubscriptionMode.BLIK,"-created_at"));
        assertEquals("/payment/svc/subscriptions?page=1&perPage=20&filter%5Buuid%5D=u&filter%5Bstatus%5D=subscription_active&filter%5Bmode%5D=BLIK&sort=-created_at",request().getPath());
        var options=new PaymentModels.BlikSubscriptionOptions(PaymentModels.BlikModel.A, LocalDate.parse("2027-01-01"),"monthly",BigDecimal.TEN,LocalDate.parse("2026-01-01"),BigDecimal.valueOf(100),null);
        var create=new PaymentModels.CreateSubscriptionRequest("tx",new PaymentModels.BlikTicket("123456"),new PaymentModels.BlikAlias("v",null,null),options,PaymentModels.descriptions("hello"),null);
        json("{\"success\":true,\"data\":{\"subscriptionId\":\"sub\",\"aliasId\":\"alias\"}}");
        assertEquals("sub",payment.createBlikSubscription(create).subscriptionId());
        var createRequest = request();
        assertEquals("POST", createRequest.getMethod());
        assertEquals("/payment/svc/blik/subscriptions", createRequest.getPath());
        var createBody = createRequest.getBody().readUtf8();
        assertTrue(createBody.contains("\"transactionId\":\"tx\""));
        assertTrue(createBody.contains("\"ticket\":{\"T6\":\"123456\"}"));
        assertTrue(createBody.contains("\"model\":\"A\""));

        json("{\"success\":true,\"data\":{\"needsUserConfirmation\":true}}");
        assertTrue(payment.blikAutoPayment("sub",new PaymentModels.AutoPaymentRequest("tx",null,1,null)).needsUserConfirmation());
        var autoPaymentRequest = request();
        assertEquals("POST", autoPaymentRequest.getMethod());
        assertEquals("/payment/svc/blik/subscriptions/sub/autopayment", autoPaymentRequest.getPath());
        assertEquals("{\"transactionId\":\"tx\",\"attempt\":1}", autoPaymentRequest.getBody().readUtf8());

        json("{\"data\":[],\"pagination\":{\"total\":0,\"count\":0,\"per_page\":20,\"current_page\":1,\"total_pages\":0,\"links\":{}}}");
        payment.blikAliases(new PaymentModels.AliasQuery(2, 30, PaymentModels.AliasStatus.ACTIVE, PaymentModels.AliasType.PAYID, "uuid", "value", "-created_at"));
        assertEquals("/payment/svc/blik/aliases?page=2&perPage=30&filter%5Bstatus%5D=alias_active&filter%5Btype%5D=PAYID&filter%5Buuid%5D=uuid&filter%5Bvalue%5D=value&sort=-created_at",request().getPath());
    }
    @Test void semanticErrors() {
        server.enqueue(new MockResponse().setResponseCode(400).setBody("{\"errorCode\":\"BLIK_CODE_EXPIRED\",\"message\":\"expired\"}")); assertThrows(InvalidBlikTicketException.class,()->payment.initiateBlikLevel0("tx","1"));
        server.enqueue(new MockResponse().setResponseCode(401).setBody("{}")); assertThrows(SimPayAuthorizationException.class,payment::channels);
        server.enqueue(new MockResponse().setResponseCode(404).setBody("{}")); assertThrows(SimPayNotFoundException.class,payment::channels);
        server.enqueue(new MockResponse().setResponseCode(422).setBody("{\"errors\":{\"amount\":[\"required\"]}}")); var e=assertThrows(SimPayValidationException.class,payment::channels); assertEquals("required",e.getErrors().get("amount").get(0));
    }

    @Test void deserializesNonEmptySubscriptionAndAliasResponses() throws Exception {
        json("{\"data\":[{\"id\":\"sub\",\"status\":\"subscription_active\",\"mode\":\"BLIK\"," +
                "\"blik\":{\"model\":\"A\",\"alias\":null},\"frequency\":\"monthly\"," +
                "\"initiation_date\":\"2026-01-01\",\"total_amount_limit\":100.00," +
                "\"total_transactions_limit\":10,\"cancelled\":null," +
                "\"created_at\":\"2026-01-01T00:00:00Z\",\"updated_at\":\"2026-01-02T00:00:00Z\"}]," +
                "\"pagination\":{\"total\":1,\"count\":1,\"per_page\":20,\"current_page\":1,\"total_pages\":1,\"links\":{}}}");
        var subscription = payment.subscriptions(new PaymentModels.SubscriptionQuery()).data().get(0);
        assertEquals(PaymentModels.SubscriptionStatus.ACTIVE, subscription.status());
        assertEquals(PaymentModels.BlikModel.A, subscription.blik().model());
        assertEquals(new BigDecimal("100.00"), subscription.totalAmountLimit());
        request();

        json("{\"data\":[{\"id\":\"alias\",\"type\":\"PAYID\",\"value\":\"value\",\"label\":\"Telefon\"," +
                "\"blik_key\":\"key\",\"status\":\"alias_active\",\"expires_at\":null," +
                "\"created_at\":\"2026-01-01T00:00:00Z\",\"updated_at\":\"2026-01-02T00:00:00Z\",\"subscription\":null}]," +
                "\"pagination\":{\"total\":1,\"count\":1,\"per_page\":20,\"current_page\":1,\"total_pages\":1,\"links\":{}}}");
        var alias = payment.blikAliases(new PaymentModels.AliasQuery()).data().get(0);
        assertEquals(PaymentModels.AliasType.PAYID, alias.type());
        assertEquals(PaymentModels.AliasStatus.ACTIVE, alias.status());
        request();
    }
    @Test void validationUnicodeAndSignatureVector() {
        assertThrows(IllegalArgumentException.class,()->new PaymentModels.SubscriptionQuery(0,20,null,null,null,null));
        assertThrows(IllegalArgumentException.class,()->new PaymentModels.BlikSubscriptionOptions(PaymentModels.BlikModel.A,null,null,null,null,null,null));
        var text="😀".repeat(80); var lines=PaymentModels.descriptions(text); assertEquals(35,lines.get("line1").codePointCount(0,lines.get("line1").length())); assertEquals(10,lines.get("line3").codePointCount(0,lines.get("line3").length()));
        var amount=new LinkedHashMap<String,Object>(); amount.put("value","10.00"); amount.put("currency","PLN");
        var payload=new LinkedHashMap<String,Object>(); payload.put("id","tx"); payload.put("amount",amount); payload.put("signature","138feceb0cc93786ec0b9b521b87aaa7fb0565f93ddf8115b75c6de92da8a427");
        assertTrue(payment.ipnSignatureValid(payload));
    }

    @Test void signatureMatchesPhpScalarRulesForBooleansNullsAndDecimals() {
        var payload = new LinkedHashMap<String,Object>();
        payload.put("id", "tx");
        payload.put("flags", java.util.Arrays.asList(true, false, null));
        payload.put("amount", new BigDecimal("10.00"));
        payload.put("signature", "343ab2fe4090ef9d219c6da2e1524fcb46b10a971c0956680c60a397bd4480e4");

        assertTrue(payment.ipnSignatureValid(payload));
    }

    @Test void signatureMatchesPhpScientificNumberFormatting() {
        var payload = new LinkedHashMap<String,Object>();
        payload.put("id", "tx");
        payload.put("numbers", List.of(1.0E-7, 1.0E+20));
        payload.put("signature", "db756e4f534e0c4891a2be8a92ac76cbf5db4a993e42c36a6cf210e9e67d70d1");

        assertTrue(payment.ipnSignatureValid(payload));
    }

    @Test void validatesCurrentDirectBillingSignatureWithExactDecimalText() {
        var notification = "{" +
                "\"id\":\"tx-1\"," +
                "\"service_id\":\"d151e4f9\"," +
                "\"status\":\"transaction_db_payed\"," +
                "\"values\":{\"net\":10.00,\"gross\":12.30,\"partner\":2.30}," +
                "\"returns\":{\"complete\":\"https://ok\",\"failure\":\"https://fail\"}," +
                "\"control\":\"order-1\"," +
                "\"number_from\":\"48123123123\"," +
                "\"provider\":7," +
                "\"signature\":\"072d1bebd775243a38526eec65afb4a9cfd7d365d500fc442a9586972359bd95\"" +
                "}";

        assertTrue(new DirectBilling(RestService.bearer("token", server.url("/"), new OkHttpClient.Builder()))
                .checkSignature("secret", notification));
    }

    @Test void directBillingSignatureHandlesMissingOptionalNotificationFields() {
        var notification = "{" +
                "\"id\":\"tx-2\",\"service_id\":\"svc\",\"status\":\"transaction_db_payed\"," +
                "\"values\":{\"net\":10.00,\"gross\":12.30,\"partner\":2.30}," +
                "\"returns\":{\"complete\":null,\"failure\":null},\"control\":null,\"number_from\":null," +
                "\"provider\":7,\"signature\":\"6841545edb0c2496b4250663d1c81986494f8ff283c5a508c67b9c843c97d490\"}";
        var directBilling = new DirectBilling(RestService.bearer("token", server.url("/"), new OkHttpClient.Builder()));

        assertTrue(directBilling.checkSignature("secret", notification));
        assertFalse(directBilling.checkSignature("secret", "not-json"));
    }

    /**
     * Notifications as SimPay's IPN sender builds them: snake_case service_id, optional fields
     * omitted instead of null, amounts as PHP prints floats. Signed with the sender's algorithm.
     */
    @Test void directBillingSignatureMatchesNotificationsSentBySimPay() {
        var directBilling = new DirectBilling(RestService.bearer("token", server.url("/"), new OkHttpClient.Builder()));
        var full = "{\"id\":\"a1b2c3\",\"service_id\":\"f1e2d3c4\",\"status\":\"transaction_db_payed\",\"values\":{\"net\":10,\"gross\":12.3,\"partner\":7.45},\"returns\":{\"complete\":\"https:\\/\\/shop.example\\/ok\",\"failure\":\"https:\\/\\/shop.example\\/fail\"},\"control\":\"order-15\",\"number_from\":\"48123456789\",\"provider\":1,\"signature\":\"a9623d6d0a3186417f36e365ddfe8346c92025009b8cf8b1a7e3f912ec3b43ad\"}";
        var minimal = "{\"id\":\"d4e5f6\",\"service_id\":42,\"status\":\"transaction_db_confirmed\",\"values\":{\"net\":0.5,\"gross\":0.62,\"partner\":0.25},\"signature\":\"38ccd5c05e5a8bb5cb9d27e57a531fffbdca16500ea68d64df74ed08c4b63c29\"}";

        assertTrue(directBilling.checkSignature("secret-key", full));
        assertTrue(directBilling.checkSignature("secret-key", minimal));
        assertFalse(directBilling.checkSignature("other-key", full));
        assertFalse(directBilling.checkSignature("secret-key", full.replace("\"gross\":12.3", "\"gross\":123")));
    }

    /** Body shaped like the DirectBilling transaction list response. */
    @Test void directBillingTransactionListReadsNetValue() throws Exception {
        var rest = RestService.bearer("token", server.url("/"), new OkHttpClient.Builder());
        json("{\"success\":true,\"data\":[{" +
                "\"id\":\"tx-1\",\"status\":\"transaction_db_confirmed\",\"value\":42.86,\"value_net\":30," +
                "\"value_created\":0,\"value_created_type\":null,\"operator\":\"orange\"," +
                "\"created_at\":\"2021-11-17T02:53:21+01:00\",\"updated_at\":\"2021-11-17T02:53:21+01:00\"}]," +
                "\"pagination\":{\"total\":1,\"count\":1,\"per_page\":15,\"current_page\":1,\"total_pages\":1," +
                "\"links\":{\"next_page\":null,\"prev_page\":null}}}");

        var transactions = new DirectBilling(rest).getTransactions("d151e4f9").data();

        assertEquals(1, transactions.size());
        assertEquals(30.0, transactions.iterator().next().netValue());
    }

    /** Body shaped like the DirectBilling service details response. */
    @Test void directBillingServiceDetailsReadsCommissions() throws Exception {
        var rest = RestService.bearer("token", server.url("/"), new OkHttpClient.Builder());
        json("{\"success\":true,\"data\":{\"id\":\"d151e4f9\",\"name\":\"Test\",\"suffix\":\"TEST\"," +
                "\"status\":\"service_active\",\"api\":{\"complete\":null,\"failure\":null}," +
                "\"providers\":{\"t-mobile\":true,\"orange\":true,\"play\":false,\"plus\":true}," +
                "\"commissions\":{\"t-mobile\":{\"commission_0\":\"0.60\",\"commission_9\":\"0.65\",\"commission_25\":\"0.70\"}," +
                "\"orange\":{\"commission_0\":\"0.55\",\"commission_9\":\"0.60\",\"commission_25\":\"0.66\"}," +
                "\"play\":{\"commission_0\":null,\"commission_9\":null,\"commission_25\":null}," +
                "\"plus\":{\"commission_0\":\"0.50\",\"commission_9\":\"0.55\",\"commission_25\":\"0.60\"}}," +
                "\"maxValues\":{\"t-mobile\":\"25.00\",\"orange\":\"25.00\",\"play\":null,\"plus\":\"25.00\"}," +
                "\"created_at\":\"2021-11-17T02:53:21+01:00\"}}");

        var service = new DirectBilling(rest).getServiceDetails("d151e4f9").data();

        assertEquals("0.65", service.commissionsPercents().tMobile().commission9());
        assertEquals("0.66", service.commissionsPercents().orange().commission25());
        assertEquals("25.00", service.maxValues().plus());
    }

    /** Body shaped like the DirectBilling service list response, one service per status. */
    @Test void directBillingServiceListReadsEveryStatus() throws Exception {
        var rest = RestService.bearer("token", server.url("/"), new OkHttpClient.Builder());
        var statuses = java.util.List.of("service_active", "service_db_new", "service_db_blocked", "service_db_deleted",
                "service_db_second_verify", "service_db_rejected", "service_verify", "service_db_ongoing_registration");
        var items = new StringBuilder();
        for (int i = 0; i < statuses.size(); i++) {
            if (i > 0) items.append(',');
            items.append("{\"id\":\"svc-").append(i).append("\",\"name\":\"Test\",\"suffix\":\"TEST\",\"status\":\"")
                    .append(statuses.get(i)).append("\",\"created_at\":\"2021-11-17T02:53:21+01:00\"}");
        }
        json("{\"success\":true,\"data\":[" + items + "],\"pagination\":{\"total\":8,\"count\":8,\"per_page\":15," +
                "\"current_page\":1,\"total_pages\":1,\"links\":{\"next_page\":null,\"prev_page\":null}}}");

        var services = new DirectBilling(rest).getServiceList().data();

        assertEquals(java.util.Set.copyOf(statuses),
                services.stream().map(service -> service.status().getStatusName()).collect(java.util.stream.Collectors.toSet()));
    }

    @Test void supportsCurrentDirectBillingDetailsAndSmsWithoutNumber() throws Exception {
        var rest = RestService.bearer("token", server.url("/"), new OkHttpClient.Builder());
        json("{\"success\":true,\"data\":{" +
                "\"id\":\"tx-1\",\"status\":\"transaction_db_payed\",\"control\":\"order-1\"," +
                "\"value\":12.30,\"value_net\":10.00,\"value_created\":12.30," +
                "\"value_created_type\":\"gross\",\"score\":0.95," +
                "\"notify\":{\"is_send\":false,\"count\":0}}}");

        var details = new DirectBilling(rest).getTransactionDetails("d151e4f9", "tx-1").data();

        assertEquals("order-1", details.control());
        assertEquals(10.0, details.netValue());
        assertEquals("gross", details.valueCreatedType());
        assertEquals("/directbilling/d151e4f9/transactions/tx-1", request().getPath());

        json("{\"success\":true,\"data\":{\"used\":false,\"code\":\"ABC\",\"test\":false," +
                "\"from\":\"48123123123\",\"number\":7055,\"value\":1.0}}");
        assertEquals("48123123123", new Sms(rest).verifyCode("e14f8074", "ABC").data().from());
        var smsRequest = request();
        assertEquals("/sms/e14f8074", smsRequest.getPath());
        assertEquals("{\"code\":\"ABC\"}", smsRequest.getBody().readUtf8());
    }
}
