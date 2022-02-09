package pl.simpay.api.adapter;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import pl.simpay.api.model.sms.ServiceStatus;

public class SmsServiceStatusAdapter implements Adapter<ServiceStatus> {

    @ToJson
    public String toJson(ServiceStatus serviceStatus) {
        return serviceStatus.getStatusName();
    }

    @FromJson
    public ServiceStatus fromJson(String statusName) {
        return ServiceStatus.getByName(statusName);
    }

}
