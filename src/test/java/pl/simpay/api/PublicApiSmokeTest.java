package pl.simpay.api;

import org.junit.jupiter.api.Test;
import pl.simpay.api.model.payment.PaymentModels;
import pl.simpay.api.payments.Simpay;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Compiles and exercises the same public entrypoints that a consuming application uses. */
class PublicApiSmokeTest {
    @Test
    void constructsLegacyClientFromExternalPackage() {
        var client = new Simpay("api-key", "api-password");

        assertNotNull(client.getSms());
        assertNotNull(client.getDirectBilling());
        assertNotNull(client.getSmsXml("hash"));
    }

    @Test
    void constructsBearerPaymentClientFromExternalPackage() {
        var client = new Simpay("bearer-token");
        var payment = client.getPayment("e14f8074", "ipn-hash");
        var request = new PaymentModels.GenerateTransactionRequest(new BigDecimal("10.00"));

        assertNotNull(payment);
        assertNotNull(request);
        assertNotNull(client.getPayment("e14f8074"));
        assertNotNull(client.getPayment().forService("e14f8074"));
        assertNotNull(client.getPayment().forService("e14f8074", "ipn-hash"));
    }
}
