import pl.simpay.api.model.sms.request.CodeVerifyRequest;
import pl.simpay.api.model.sms.request.ServiceListRequest;
import pl.simpay.api.payments.Sms;

public class SmsTest {
    private static final String API_KEY = "XXXXXXXX";
    private static final String API_SECRET = "XXXXXXXXXXXXXX";
    private static final String SERVICE_ID = "XXXXX";

    public static void main(String[] args) {
        verifyCode();
        getServiceList();
    }

    private static void verifyCode() {
        Sms sms = new Sms(API_KEY, API_SECRET, SERVICE_ID);

        CodeVerifyRequest request = new CodeVerifyRequest();
        request.setNumber("7055");
        request.setCode("4C5ZFE");
        request.setService_id("3487");

        System.out.println(sms.verifyCode(request));
    }

    private static void getServiceList() {
        Sms sms = new Sms(API_KEY, API_SECRET, SERVICE_ID);

        ServiceListRequest request = new ServiceListRequest();

        System.out.println(sms.getServiceList(request));
    }
}
