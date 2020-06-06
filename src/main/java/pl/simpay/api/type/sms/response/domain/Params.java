package pl.simpay.api.type.sms.response.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Params {

    @SerializedName("service_id")
    @Expose
    private String serviceId;
    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("secret")
    @Expose
    private String secret;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

}
