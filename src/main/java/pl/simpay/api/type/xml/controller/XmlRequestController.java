package pl.simpay.api.type.xml.controller;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.simpay.api.type.xml.domain.XmlRequest;
import pl.simpay.api.type.xml.service.XmlRequestBuilder;
import pl.simpay.api.type.xml.service.XmlRequestStatus;
import pl.simpay.api.type.xml.service.XmlRequestVerifier;
import pl.simpay.api.type.xml.service.XmlResponseService;

@RestController
public class XmlRequestController {

    private XmlResponseService xmlResponseService;

    public XmlRequestController(XmlResponseService xmlResponseService) {
        this.xmlResponseService = xmlResponseService;
    }

    @PostMapping(value = "/simpay_xml")
    public String getXmlRequest(@RequestParam MultiValueMap<String, String> requestData) {
        XmlRequest xmlRequest = XmlRequestBuilder.buildXmlRequest(requestData);
        XmlRequestStatus xmlRequestStatus = XmlRequestVerifier.getStatus(xmlRequest);

        return XmlRequestStatus.OK.equals(xmlRequestStatus)
                ? xmlResponseService.createMessage(xmlRequest)
                : xmlRequestStatus.name();
    }
}
