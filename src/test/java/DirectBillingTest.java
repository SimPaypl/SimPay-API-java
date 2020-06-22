import pl.simpay.api.model.db.requests.DbGenerateRequest;
import pl.simpay.api.model.db.requests.DbServiceCommissionRequest;
import pl.simpay.api.model.db.requests.DbServicesListRequest;
import pl.simpay.api.model.db.requests.DbTransactionLimitsRequest;
import pl.simpay.api.payments.DirectBilling;

public class DirectBillingTest {
    private static final String API_KEY = "XXXXXXX";
    private static final String API_SECRET = "XXXXXXXXX";
    private static final String SERVICE_ID = "XXXXXXX";

    public static void main(String[] args) {
        getServersIp();
        getTransactionLimits();
        getServices();
        getServiceCommission();
        generateTransaction();
    }

    private static void getServersIp() {
        DirectBilling db = new DirectBilling(API_KEY, API_SECRET, true, SERVICE_ID);

        System.out.println(db.getServersIp());
    }

    private static void getTransactionLimits() {
        DirectBilling db = new DirectBilling(API_KEY, API_SECRET, true, SERVICE_ID);

        System.out.println(db.getTransactionLimits(new DbTransactionLimitsRequest()));
    }

    private static void getServices() {
        DirectBilling db = new DirectBilling(API_KEY, API_SECRET, true, SERVICE_ID);

        System.out.println(db.getServices(new DbServicesListRequest()));
    }

    private static void getServiceCommission() {
        DirectBilling db = new DirectBilling(API_KEY, API_SECRET, true, SERVICE_ID);

        System.out.println(db.getServiceCommission(new DbServiceCommissionRequest()));
    }

    private static void generateTransaction() {
        DirectBilling db = new DirectBilling("pqDxnsRFe6godfT0", API_SECRET, true, SERVICE_ID);

        DbGenerateRequest request = new DbGenerateRequest();

        request.setControl("XXXXXXX");
        request.setAmount("12.50");

        System.out.println(db.generateTransaction(request));
    }
}
