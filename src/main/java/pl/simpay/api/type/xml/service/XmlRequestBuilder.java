package pl.simpay.api.type.xml.service;

import org.springframework.util.MultiValueMap;
import pl.simpay.api.type.xml.domain.XmlRequest;

public class XmlRequestBuilder {

    private XmlRequestBuilder() {
        //empty
    }

    public static XmlRequest buildXmlRequest(MultiValueMap<String, String> responseData) {
        return new XmlRequest(
                responseData.getFirst("sms_id"),
                responseData.getFirst("sms_from"),
                responseData.getFirst("send_number"),
                responseData.getFirst("sms_text"),
                responseData.getFirst("send_time"),
                responseData.getFirst("sign"));
    }
}
