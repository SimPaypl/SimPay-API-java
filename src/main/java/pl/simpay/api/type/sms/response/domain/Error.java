package pl.simpay.api.type.sms.response.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Error {

    @SerializedName("error_code")
    @Expose
    private Integer errorCode;
    @SerializedName("error_name")
    @Expose
    private String errorName;
    @SerializedName("error_value")
    @Expose
    private String errorValue;

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getErrorValue() {
        return errorValue;
    }

    public void setErrorValue(String errorValue) {
        this.errorValue = errorValue;
    }

}
