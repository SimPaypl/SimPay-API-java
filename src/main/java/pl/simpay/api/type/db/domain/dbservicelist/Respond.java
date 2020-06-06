
package pl.simpay.api.type.db.domain.dbservicelist;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Respond {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("services")
    @Expose
    private List<Service> services = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

}
