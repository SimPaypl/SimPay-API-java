package pl.simpay.api.model.request;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.simpay.api.util.Sha256;

import java.util.List;
import com.squareup.moshi.Json;
import java.util.Objects;

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
        Objects.requireNonNull(key, "key");
        var s = amount + "|" + String.join("|", List.of(
                value(amountType),
                value(description),
                value(control),
                returns == null ? "" : value(returns.success()),
                returns == null ? "" : value(returns.failure()),
                value(phoneNumber),
                key
        ));
        this.signature = Sha256.hash(s);
    }

    public void signWithAmountAndControl(String key) {
        Objects.requireNonNull(key, "key");
        var s = amount + "|" + String.join("|", List.of(value(control), key));
        this.signature = Sha256.hash(s);
    }

    private static String value(String value) {
        return value == null ? "" : value;
    }
}
