package pl.simpay.api.model.request;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.simpay.api.util.Sha256;

import java.util.List;
import com.squareup.moshi.Json;

@Builder
@ToString
@EqualsAndHashCode
public class GenerateTransactionRequest {
    private final double amount;
    @Builder.Default
    private final String amountType = "gross";
    private final String description;
    private final String control;
    private final RedirectURL returns;
    private final String phoneNumber;
    @Json(name = "steamid")
    private final String steamId;
    private final String email;
    private String signature;

    public void sign(String key) {
        var s = amount + "|" + String.join("|", List.of(amountType, description, control, returns.success(), returns.failure(), phoneNumber, key));
        this.signature = Sha256.hash(s);
    }

    public void signWithAmountAndControl(String key) {
        var s = amount + "|" + String.join("|", List.of(control, key));
        this.signature = Sha256.hash(s);
    }
}
