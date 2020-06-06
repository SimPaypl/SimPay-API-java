package pl.simpay.api.type.db.domain.paymentinformation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Respond {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("valuenet")
    @Expose
    private Double valuenet;
    @SerializedName("valuenet_gross")
    @Expose
    private Double valuenetGross;
    @SerializedName("valuepartner")
    @Expose
    private Double valuepartner;
    @SerializedName("control")
    @Expose
    private String control;
    @SerializedName("number_from")
    @Expose
    private Integer numberFrom;
    @SerializedName("sign")
    @Expose
    private String sign;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getValuenet() {
        return valuenet;
    }

    public void setValuenet(Double valuenet) {
        this.valuenet = valuenet;
    }

    public Double getValuenetGross() {
        return valuenetGross;
    }

    public void setValuenetGross(Double valuenetGross) {
        this.valuenetGross = valuenetGross;
    }

    public Double getValuepartner() {
        return valuepartner;
    }

    public void setValuepartner(Double valuepartner) {
        this.valuepartner = valuepartner;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public Integer getNumberFrom() {
        return numberFrom;
    }

    public void setNumberFrom(Integer numberFrom) {
        this.numberFrom = numberFrom;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

}
