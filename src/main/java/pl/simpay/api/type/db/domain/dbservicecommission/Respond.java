package pl.simpay.api.type.db.domain.dbservicecommission;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Respond {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("commission_0")
    @Expose
    private String commission0;
    @SerializedName("commission_9")
    @Expose
    private String commission9;
    @SerializedName("commission_25")
    @Expose
    private String commission25;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCommission0() {
        return commission0;
    }

    public void setCommission0(String commission0) {
        this.commission0 = commission0;
    }

    public String getCommission9() {
        return commission9;
    }

    public void setCommission9(String commission9) {
        this.commission9 = commission9;
    }

    public String getCommission25() {
        return commission25;
    }

    public void setCommission25(String commission25) {
        this.commission25 = commission25;
    }

}
