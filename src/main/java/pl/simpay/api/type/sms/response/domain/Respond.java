package pl.simpay.api.type.sms.response.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Respond {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("test")
    @Expose
    private Integer test;
    @SerializedName("from")
    @Expose
    private Integer from;
    @SerializedName("number")
    @Expose
    private Integer number;
    @SerializedName("value")
    @Expose
    private Integer value;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTest() {
        return test;
    }

    public void setTest(Integer test) {
        this.test = test;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}