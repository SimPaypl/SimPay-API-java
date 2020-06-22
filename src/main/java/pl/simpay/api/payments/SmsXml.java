package pl.simpay.api.payments;

import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import pl.simpay.api.model.generic.APIResponse;
import pl.simpay.api.model.generic.IPResponse;
import pl.simpay.api.utils.Hashing;
import pl.simpay.api.utils.HttpService;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Data
@AllArgsConstructor
public class SmsXml {
    private static final HttpService service = new HttpService();
    private static final String GET_IP_URL = "https://simpay.pl/api/get_ip";
    private static final String charset = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789";
    private static final String[] params = new String[]{"send_number", "sms_text", "sms_from", "sms_id", "sign"};
    private static final Map<String, Double> codes = new HashMap<>();

    static {
        codes.put("7055", 0.25);
        codes.put("7136", 0.5);
        codes.put("7255", 1.0);
        codes.put("7355", 1.5);
        codes.put("7455", 2.0);
        codes.put("7555", 2.5);
        codes.put("7636", 3.0);
        codes.put("77464", 3.5);
        codes.put("78464", 4.0);
        codes.put("7936", 4.5);
        codes.put("91055", 5.0);
        codes.put("91155", 5.5);
        codes.put("91455", 7.0);
        codes.put("91664", 8.0);
        codes.put("91955", 9.5);
        codes.put("92055", 10.0);
        codes.put("92555", 12.5);
    }

    private static final TypeToken<APIResponse<IPResponse>> IP_RESPONSE = new TypeToken<APIResponse<IPResponse>>() {};

    private String apiKey;

    // https://docs.simpay.pl/#odbieranie-informacji-o-sms
    public boolean checkParameters(Map<String, Object> map) {
        for (String param : params) {
            if (!map.containsKey(param)) return false;
        }

        return map.get("sign") == this.sign(map);
    }

    public String sign(Map<String, Object> map) {
        return Hashing.sha256hex(String.valueOf(map.get("sms_id")) + map.get("sms_text") + map.get("sms_from") + map.get("send_number") + map.get("send_time") + apiKey);
    }

    // https://docs.simpay.pl/#odbieranie-informacji-o-sms
    public String generateCode() {
        int length = 6;

        StringBuilder result = new StringBuilder();

        while (length-- > 0) {
            result.append(charset.charAt(ThreadLocalRandom.current().nextInt(0, charset.length())));
        }

        return result.toString();
    }

    // https://docs.simpay.pl/#odbieranie-informacji-o-sms
    public double getSmsValue(String number) {
        return codes.get(number);
    }

    // https://docs.simpay.pl/#odbieranie-informacji-o-sms
    public String generateXml(String text) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><sms-response>" + Normalizer.normalize(text, Normalizer.Form.NFKD) + "<sms-text></sms-text></sms-response>";
    }

    // https://docs.simpay.pl/#lista-ip-serwerow-simpay
    public boolean getServersIp(String ip) {
        IPResponse ipResponse = service.sendGet(GET_IP_URL, IP_RESPONSE.getType());
        return ipResponse.getIps().contains(ip);
    }
}
