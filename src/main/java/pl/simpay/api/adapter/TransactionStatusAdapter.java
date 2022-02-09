package pl.simpay.api.adapter;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import pl.simpay.api.model.directBilling.transaction.TransactionStatus;

public class TransactionStatusAdapter implements Adapter<TransactionStatus> {

    @ToJson
    public String toJson(TransactionStatus transactionStatus) {
        return transactionStatus.getStatusName();
    }

    @FromJson
    public TransactionStatus fromJson(String statusName) {
        return TransactionStatus.getByName(statusName);
    }
}
