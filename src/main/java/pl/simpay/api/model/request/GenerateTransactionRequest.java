package pl.simpay.api.model.request;

import com.google.common.hash.Hashing;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Builder
@ToString
@EqualsAndHashCode
public class GenerateTransactionRequest {
    private final double amount;
    private final String amountType;
    private final String description;
    private final String control;
    private final RedirectURL returns;
    private final String phoneNumber;
    private String signature;

    public void sign(String key) {
        var s = amount + "|" + String.join("|", List.of(amountType, description, control, returns.success(), returns.failure(), phoneNumber, key));
        this.signature = Hashing.sha256().hashString(s, StandardCharsets.UTF_8).toString();
    }

    public void signWithAmountAndControl(String key) {
        var s = amount + "|" + String.join("|", List.of(control, key));
        this.signature = Hashing.sha256().hashString(s, StandardCharsets.UTF_8).toString();
    }
}
