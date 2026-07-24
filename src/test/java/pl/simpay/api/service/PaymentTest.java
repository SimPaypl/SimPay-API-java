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
        json("{\"success\":true,\"data\":{\"id\":\"tx\",\"status\":\"transaction_paid\",\"amount\":{\"value\":10.20,\"currency\":\"PLN\"},\"payer_transaction_id\":\"payer\"}}");
        assertEquals(PaymentModels.TransactionStatus.PAID,payment.transactionInfo("tx").status()); assertEquals("GET",request().getMethod());
    }
    @Test void channelsAndCurrencies() throws Exception {
        json("{\"success\":true,\"data\":[{\"id\":\"blik\",\"name\":\"BLIK\",\"type\":\"blik\",\"img\":\"x\",\"currencies\":[\"PLN\"],\"amount\":{\"min\":1,\"max\":100}}]}");
        assertEquals("blik",payment.channels().get(0).id()); assertEquals("/payment/svc/channels",request().getPath());
        json("{\"success\":true,\"data\":[{\"iso\":\"PLN\",\"pln_rate\":\"1.0\",\"updated_at\":\"2026-01-01T00:00:00Z\"}]}");
        assertEquals("PLN",payment.currencies().get(0).iso()); assertEquals("/payment/svc/currencies",request().getPath());
    }
    @Test void blikAndAliasNoContent() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(204)); assertTrue(payment.initiateBlikLevel0("tx","123456"));
        var r=request(); assertEquals("POST",r.getMethod()); assertEquals("/payment/svc/blik/level0/tx",r.getPath()); assertEquals("{\"ticket\":{\"T6\":\"123456\"}}",r.getBody().readUtf8());
        server.enqueue(new MockResponse().setResponseCode(204)); assertTrue(payment.unregisterBlikAlias(new PaymentModels.UnregisterAliasRequest(null,"uuid",null,"done")));
        r=request(); assertEquals("DELETE",r.getMethod()); assertEquals("{\"uuid\":\"uuid\",\"reason\":\"done\"}",r.getBody().readUtf8());
    }
    @Test void subscriptionOperationsAndQueries() throws Exception {
        json("{\"data\":[],\"pagination\":{\"total\":0,\"count\":0,\"per_page\":20,\"current_page\":1,\"total_pages\":0,\"links\":{}}}");
        payment.subscriptions(new PaymentModels.SubscriptionQuery(1,20,"u",PaymentModels.SubscriptionStatus.ACTIVE,PaymentModels.SubscriptionMode.BLIK,"-created_at"));
        assertEquals("/payment/svc/subscriptions?page=1&perPage=20&filter%5Buuid%5D=u&filter%5Bstatus%5D=subscription_active&filter%5Bmode%5D=BLIK&sort=-created_at",request().getPath());
        var options=new PaymentModels.BlikSubscriptionOptions(PaymentModels.BlikModel.A, LocalDate.parse("2027-01-01"),"monthly",BigDecimal.TEN,LocalDate.parse("2026-01-01"),BigDecimal.valueOf(100),null);
        var create=new PaymentModels.CreateSubscriptionRequest("tx",new PaymentModels.BlikTicket("123456"),new PaymentModels.BlikAlias("v",null,null),options,PaymentModels.descriptions("hello"),null);
        json("{\"success\":true,\"data\":{\"subscriptionId\":\"sub\",\"aliasId\":\"alias\"}}"); assertEquals("sub",payment.createBlikSubscription(create).subscriptionId()); assertEquals("/payment/svc/blik/subscriptions",request().getPath());
        json("{\"success\":true,\"data\":{\"needsUserConfirmation\":true}}"); assertTrue(payment.blikAutoPayment("sub",new PaymentModels.AutoPaymentRequest("tx",null,1,null)).needsUserConfirmation()); assertEquals("/payment/svc/blik/subscriptions/sub/autopayment",request().getPath());
        json("{\"data\":[],\"pagination\":{\"total\":0,\"count\":0,\"per_page\":20,\"current_page\":1,\"total_pages\":0,\"links\":{}}}"); payment.blikAliases(new PaymentModels.AliasQuery()); assertEquals("/payment/svc/blik/aliases?page=1&perPage=20",request().getPath());
    }
    @Test void semanticErrors() {
        server.enqueue(new MockResponse().setResponseCode(400).setBody("{\"errorCode\":\"BLIK_CODE_EXPIRED\",\"message\":\"expired\"}")); assertThrows(InvalidBlikTicketException.class,()->payment.initiateBlikLevel0("tx","1"));
        server.enqueue(new MockResponse().setResponseCode(401).setBody("{}")); assertThrows(SimPayAuthorizationException.class,payment::channels);
        server.enqueue(new MockResponse().setResponseCode(404).setBody("{}")); assertThrows(SimPayNotFoundException.class,payment::channels);
        server.enqueue(new MockResponse().setResponseCode(422).setBody("{\"errors\":{\"amount\":[\"required\"]}}")); var e=assertThrows(SimPayValidationException.class,payment::channels); assertEquals("required",e.getErrors().get("amount").get(0));
    }
    @Test void validationUnicodeAndSignatureVector() {
        assertThrows(IllegalArgumentException.class,()->new PaymentModels.SubscriptionQuery(0,20,null,null,null,null));
        assertThrows(IllegalArgumentException.class,()->new PaymentModels.BlikSubscriptionOptions(PaymentModels.BlikModel.A,null,null,null,null,null,null));
        var text="😀".repeat(80); var lines=PaymentModels.descriptions(text); assertEquals(35,lines.get("line1").codePointCount(0,lines.get("line1").length())); assertEquals(10,lines.get("line3").codePointCount(0,lines.get("line3").length()));
        var amount=new LinkedHashMap<String,Object>(); amount.put("value","10.00"); amount.put("currency","PLN");
        var payload=new LinkedHashMap<String,Object>(); payload.put("id","tx"); payload.put("amount",amount); payload.put("signature","138feceb0cc93786ec0b9b521b87aaa7fb0565f93ddf8115b75c6de92da8a427");
        assertTrue(payment.ipnSignatureValid(payload));
    }

    @Test void validatesCurrentDirectBillingSignatureWithExactDecimalText() {
        var notification = "{" +
                "\"id\":\"tx-1\"," +
                "\"serviceId\":\"d151e4f9\"," +
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
