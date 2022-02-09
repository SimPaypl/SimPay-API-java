package pl.simpay.api.payments;

import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class SmsXml {
    private static final String charset = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789";
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
        var params = List.of("send_number", "sms_text", "sms_from", "sms_id", "sign");
        for (String param : params) {
            if (!map.containsKey(param)) return false;
        }

        return map.get("sign") == this.sign(map);
    }

    public String sign(Map<String, Object> map) {
        return Hashing.sha256().hashString(String.valueOf(map.get("sms_id")) + map.get("sms_text") + map.get("sms_from") + map.get("send_number") + map.get("send_time") + hashingKey, StandardCharsets.UTF_8).toString();
    }

    public String generateCode() {
        int length = 6;

        StringBuilder result = new StringBuilder();

        while (length-- > 0) {
            result.append(charset.charAt(ThreadLocalRandom.current().nextInt(0, charset.length())));
        }

        return result.toString();
    }

    public double getSmsValue(String number) {
        return codes.get(number);
    }

    public String generateXml(String text) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><sms-response>" + Normalizer.normalize(text, Normalizer.Form.NFKD) + "<sms-text></sms-text></sms-response>";
    }

}
