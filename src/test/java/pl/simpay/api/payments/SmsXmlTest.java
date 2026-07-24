package pl.simpay.api.payments;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmsXmlTest {

    private final SmsXml smsXml = new SmsXml("secret-key");

    @Test
    void acceptsValidSignatureStoredInSeparateStringInstance() {
        var parameters = validParameters();
        parameters.put("sign", new String(smsXml.sign(parameters)));

        assertTrue(smsXml.checkParameters(parameters));
    }

    @Test
    void matchesKnownSignatureVector() {
        var parameters = validParameters();
        var expectedSignature = "2ccde5509fe80801ed85dea5519b4fdffda5a63e9721738b9d60f13b035e2a2d";

        assertEquals(expectedSignature, smsXml.sign(parameters));
        parameters.put("sign", expectedSignature);
        assertTrue(smsXml.checkParameters(parameters));

        parameters.put("send_time", "2026-07-24 12:30:01");
        assertFalse(smsXml.checkParameters(parameters));
    }

    @Test
    void rejectsMissingSendTime() {
        var parameters = validParameters();
        parameters.remove("send_time");
        parameters.put("sign", smsXml.sign(parameters));

        assertFalse(smsXml.checkParameters(parameters));
    }

    @Test
    void rejectsMalformedSignature() {
        var parameters = validParameters();
        parameters.put("sign", "not-a-sha256-signature");

        assertFalse(smsXml.checkParameters(parameters));
    }

    @Test
    void rejectsIncorrectWellFormedSignature() {
        var parameters = validParameters();
        parameters.put("sign", "0".repeat(64));

        assertFalse(smsXml.checkParameters(parameters));
    }

    @Test
    void generatesSixCharacterCodeFromSupportedAlphabet() {
        var code = smsXml.generateCode();

        assertEquals(6, code.length());
        assertTrue(code.matches("[A-Z1-9]{6}"));
    }

    @Test
    void rejectsUnknownSmsNumberExplicitly() {
        var exception = assertThrows(IllegalArgumentException.class, () -> smsXml.getSmsValue("0000"));

        assertEquals("Unsupported SMS number: 0000", exception.getMessage());
    }

    @Test
    void returnsValueForSupportedSmsNumber() {
        assertEquals(1.0, smsXml.getSmsValue("7255"));
    }

    @Test
    void generatesNormalizedXmlWithTextInsideSmsTextElement() {
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><sms-response><sms-text>Załółć</sms-text></sms-response>",
                smsXml.generateXml("Załółć")
        );
    }

    private Map<String, Object> validParameters() {
        return new HashMap<>(Map.of(
                "send_number", "7055",
                "sms_text", "TEST",
                "sms_from", "48123123123",
                "sms_id", "123456",
                "send_time", "2026-07-24 12:30:00"
        ));
    }
}
