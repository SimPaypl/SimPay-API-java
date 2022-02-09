package pl.simpay.api.adapter;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import pl.simpay.api.model.directBilling.transaction.Operator;

public class OperatorAdapter implements Adapter<Operator> {

    @ToJson
    public String toJson(Operator operator) {
        return operator.getOperatorName();
    }

    @FromJson
    public Operator fromJson(String operatorName) {
        return Operator.getByName(operatorName);
    }
}
