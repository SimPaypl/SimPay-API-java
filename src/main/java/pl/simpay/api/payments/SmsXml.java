package pl.simpay.api.payments;

import lombok.RequiredArgsConstructor;
import pl.simpay.api.util.Sha256;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.HexFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class SmsXml {
    private static final String charset = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Map<String, Double> codes = new HashMap<>() {{
        put("7055", 0.25);
        put("7136", 0.5);
        put("7255", 1.0);
        put("7355", 1.5);
        put("7455", 2.0);
        put("7555", 2.5);
        put("7636", 3.0);
        put("77464", 3.5);
        put("78464", 4.0);
        put("7936", 4.5);
        put("91055", 5.0);
        put("91155", 5.5);
        put("91455", 7.0);
        put("91664", 8.0);
        put("91955", 9.5);
        put("92055", 10.0);
        put("92555", 12.5);
    }};

    private final String hashingKey;

    public boolean checkParameters(Map<String, Object> map) {
        if (map == null) return false;

        var params = List.of("send_number", "sms_text", "sms_from", "sms_id", "send_time", "sign");
        for (String param : params) {
            if (!map.containsKey(param) || map.get(param) == null) return false;
        }

        var providedSignature = String.valueOf(map.get("sign"));
        if (!providedSignature.matches("(?i)[0-9a-f]{64}")) return false;

        var expectedSignature = sign(map);
        return MessageDigest.isEqual(
                HexFormat.of().parseHex(expectedSignature),
                HexFormat.of().parseHex(providedSignature)
        );
    }

    public String sign(Map<String, Object> map) {
        return Sha256.hash(String.valueOf(map.get("sms_id")) + map.get("sms_text") + map.get("sms_from") + map.get("send_number") + map.get("send_time") + hashingKey);
    }

    public String generateCode() {
        int length = 6;

        StringBuilder result = new StringBuilder();

        while (length-- > 0) {
            result.append(charset.charAt(RANDOM.nextInt(charset.length())));
        }

        return result.toString();
    }

    public double getSmsValue(String number) {
        var value = codes.get(number);
        if (value == null) {
            throw new IllegalArgumentException("Unsupported SMS number: " + number);
        }
        return value;
    }

    public String generateXml(String text) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><sms-response>" + Normalizer.normalize(text, Normalizer.Form.NFKD) + "<sms-text></sms-text></sms-response>";
    }

}
