package pl.simpay.api.type.db.domain.dbservicecommission;

import com.google.gson.annotations.SerializedName;

import static pl.simpay.config.ApiParameters.API_KEY;
import static pl.simpay.config.ApiParameters.API_SECRET;

public class DbServiceCommissionRequest {
    @SerializedName("service_id")
    private String serviceId;
    private String key;
    private String secret;

    public DbServiceCommissionRequest(String serviceId) {
        this.serviceId = serviceId;
        this.key = API_KEY;
        this.secret = API_SECRET;
    }
}
