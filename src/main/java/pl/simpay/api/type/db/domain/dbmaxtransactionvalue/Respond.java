package pl.simpay.api.type.db.domain.dbmaxtransactionvalue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Respond {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("max")
    @Expose
    private String max;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

}
