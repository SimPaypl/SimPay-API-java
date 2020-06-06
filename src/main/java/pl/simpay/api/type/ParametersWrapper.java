package pl.simpay.api.type;

import com.google.gson.annotations.SerializedName;

public class ParametersWrapper<T> {

    @SerializedName("params")
    private T params;

    public ParametersWrapper(T params) {
        this.params = params;
    }
}
