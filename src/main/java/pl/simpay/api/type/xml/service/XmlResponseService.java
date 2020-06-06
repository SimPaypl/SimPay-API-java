package pl.simpay.api.type.xml.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import pl.simpay.api.type.xml.domain.XmlRequest;

import java.util.HashMap;
import java.util.Map;

@Service
public class XmlResponseService {

    private static final String MESSAGE_TO_RETURN_PATTERN = "Twoj kod SMS to: {code-placeholder}";
    private static final String CODE_PLACEHOLDER = "{code-placeholder}";
    private static final String XML_MESSAGE_PLACEHOLDER = "{message-placeholder}";
    private static final String XML_RESPONSE_PATTERN =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<sms-response>\n" +
                        "\t<sms-text>{message-placeholder}</sms-text>\n" +
                    "</sms-response>";
    private static final int CODE_LENGTH = 8;


    public String createMessage(XmlRequest xmlRequest) {
        String generatedCode = createCode();
        String smsContent = createSmsContent(generatedCode);

        return createXmlResponse(smsContent);
    }

    private String createXmlResponse(String smsContent) {
        return XmlResponseService.XML_RESPONSE_PATTERN.replace(XmlResponseService.XML_MESSAGE_PLACEHOLDER, smsContent);
    }

    private String createSmsContent(String generatedCode) {
        return MESSAGE_TO_RETURN_PATTERN.replace(CODE_PLACEHOLDER, generatedCode);
    }

    private String createCode() {
        return RandomStringUtils.randomAlphanumeric(CODE_LENGTH).toUpperCase();
    }

    private static Map<String, String> netPremiumNumberValue() {
        Map<String, String> values = new HashMap<>();
        values.put("7055", "0.50");
        values.put("7136", "1.00");
        values.put("7255", "2.00");
        values.put("7355", "3.00");
        values.put("7455", "4.00");
        values.put("7555", "5.00");
        values.put("7636", "6.00");
        values.put("77464", "7.00");
        values.put("78464", "8.00");
        values.put("7936", "9.00");
        values.put("91055", "10.00");
        values.put("91155", "11.00");
        values.put("91455", "14.00");
        values.put("91664", "16.00");
        values.put("91955", "19.00");
        values.put("92055", "20.00");
        values.put("92555", "25.00");

        return values;
    }

}
