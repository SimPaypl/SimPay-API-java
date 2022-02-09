package pl.simpay.api.model.directBilling.transaction;

import lombok.Getter;
import pl.simpay.api.exception.UnsupportedOperatorException;

import java.util.Arrays;

public enum Operator {
    ORANGE("Orange"),
    PLUS("Plus"),
    PLAY("Play"),
    T_MOBILE("T-mobile");

    @Getter
    private final String operatorName;

    Operator(String operatorName) {
        this.operatorName = operatorName;
    }

    public static Operator getByName(String name) {
        return Arrays.stream(values()).filter(operator -> operator.getOperatorName().equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new UnsupportedOperatorException(name));
    }
}
