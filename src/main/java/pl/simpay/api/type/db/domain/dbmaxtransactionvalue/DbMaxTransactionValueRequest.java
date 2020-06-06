package pl.simpay.api.type.db.domain.dbmaxtransactionvalue;

import com.google.gson.annotations.SerializedName;

import static pl.simpay.config.ApiParameters.API_KEY;
import static pl.simpay.config.ApiParameters.API_SECRET;

public class DbMaxTransactionValueRequest {
    @SerializedName("service_id")
    private String serviceId;
    private String key;
    private String secret;

    public DbMaxTransactionValueRequest(String serviceId) {
        this.serviceId = serviceId;
        this.key = API_KEY;
        this.secret = API_SECRET;
    }
}
