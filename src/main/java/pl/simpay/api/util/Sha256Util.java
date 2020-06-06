package pl.simpay.api.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import pl.simpay.api.type.db.domain.DbPaymentResponse;
import pl.simpay.api.type.xml.domain.XmlRequest;

import java.util.List;

import static pl.simpay.config.ApiParameters.API_KEY;
import static pl.simpay.config.ApiParameters.DB_KEY;

public class Sha256Util {

    private Sha256Util() {
        //empty
    }

    public static void addDbPaymentControlSign(List<NameValuePair> params, String serviceId, String amountValue, String control) {
        params.add(new BasicNameValuePair("sign", createDbSign(serviceId, amountValue, control)));
    }

    public static boolean dbPaymentResponseSignIsValid(DbPaymentResponse dbPaymentResponse) {
        String responseSignElements = dbPaymentResponse.getId()
                .concat(dbPaymentResponse.getStatus().name())
                .concat(dbPaymentResponse.getValueNet())
                .concat(dbPaymentResponse.getValuePartner())
                .concat(dbPaymentResponse.getControl())
                .concat(DB_KEY);

        return dbPaymentResponse.getSign().equals(DigestUtils.sha256Hex(responseSignElements));
    }

    public static boolean xmlRequestSignIsValid(XmlRequest xmlRequest) {
        String requestSignElements = xmlRequest.getId()
                .concat(xmlRequest.getSmsText())
                .concat(xmlRequest.getSmsFrom())
                .concat(xmlRequest.getSendNumber())
                .concat(xmlRequest.getSendTime())
                .concat(API_KEY);

        return xmlRequest.getSign().equals(DigestUtils.sha256Hex(requestSignElements));
    }

    private static String createDbSign(String serviceId, String amountValue, String control) {
        String uncodedSign = serviceId
                .concat(amountValue)
                .concat(control)
                .concat(DB_KEY);

        return DigestUtils.sha256Hex(uncodedSign);
    }
}
