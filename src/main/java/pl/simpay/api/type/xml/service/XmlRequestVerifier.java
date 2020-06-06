package pl.simpay.api.type.xml.service;

import pl.simpay.api.type.xml.domain.XmlRequest;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static pl.simpay.api.util.Sha256Util.xmlRequestSignIsValid;

public class XmlRequestVerifier {

    private XmlRequestVerifier() {
        //empty
    }

    public static XmlRequestStatus getStatus(XmlRequest xmlRequest) {
        if (isNull(xmlRequest)) {
            return XmlRequestStatus.MISSING_PARAMETERS;
        }

        if (isBlank(xmlRequest.getSign())) {
            return XmlRequestStatus.NO_SIGNATURE_PARAMETER;
        }

        if (isBlank(xmlRequest.getId())
                || isBlank(xmlRequest.getSmsFrom())
                || isBlank(xmlRequest.getSendNumber())
                || isBlank(xmlRequest.getSmsText())
                || isBlank(xmlRequest.getSendTime())) {
            return XmlRequestStatus.MISSING_PARAMETERS;
        }

        if (!xmlRequestSignIsValid(xmlRequest)) {
            return XmlRequestStatus.WRONG_SIGNATURE;
        }

        return XmlRequestStatus.OK;
    }
}
