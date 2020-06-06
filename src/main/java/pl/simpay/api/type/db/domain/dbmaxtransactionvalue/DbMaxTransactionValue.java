package pl.simpay.api.type.db.domain.dbmaxtransactionvalue;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DbMaxTransactionValue {

    @SerializedName("respond")
    @Expose
    private List<Respond> respond = null;
    @SerializedName("error")
    @Expose
    private List<Object> error = null;

    public List<Respond> getRespond() {
        return respond;
    }

    public void setRespond(List<Respond> respond) {
        this.respond = respond;
    }

    public List<Object> getError() {
        return error;
    }

    public void setError(List<Object> error) {
        this.error = error;
    }

}
