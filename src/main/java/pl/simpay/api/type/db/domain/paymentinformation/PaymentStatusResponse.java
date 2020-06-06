package pl.simpay.api.type.db.domain.paymentinformation;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentStatusResponse {

    @SerializedName("respond")
    @Expose
    private Respond respond;
    @SerializedName("error")
    @Expose
    private List<Object> error = null;

    public Respond getRespond() {
        return respond;
    }

    public void setRespond(Respond respond) {
        this.respond = respond;
    }

    public List<Object> getError() {
        return error;
    }

    public void setError(List<Object> error) {
        this.error = error;
    }

}
