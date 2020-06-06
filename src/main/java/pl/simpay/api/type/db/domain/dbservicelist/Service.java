
package pl.simpay.api.type.db.domain.dbservicelist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Service {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("sufix")
    @Expose
    private String sufix;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSufix() {
        return sufix;
    }

    public void setSufix(String sufix) {
        this.sufix = sufix;
    }

}
