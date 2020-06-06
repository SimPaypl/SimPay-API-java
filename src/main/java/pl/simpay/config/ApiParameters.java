package pl.simpay.config;

public class ApiParameters {

    private ApiParameters() {
        //empty
    }

    public static final String API_KEY = "x";
    public static final String API_SECRET = "x";

    /**
     * Kod jednorazowy
     */

    public static final String SMS_API_URL = "https://simpay.pl/api/status";

    /**
     * Direct Billing
     */

    public static final String DB_KEY = "x";
    public static final String DB_API_URL = "https://simpay.pl/db/api";
    public static final String DB_STATUS_API_URL = "https://simpay.pl/api/db_status";
    public static final String DB_SERVICES_LIST_URL = "https://simpay.pl/api/get_services_db";
    public static final String DB_MAX_TRANSACTION_VALUE_URL = "https://simpay.pl/api/db_hosts";
    public static final String DB_SERVICE_COMMISSION_URL = "https://simpay.pl/api/db_hosts_commission";
}
