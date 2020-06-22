package pl.simpay.api.model.generic;

import lombok.Getter;

public enum Operator {
    ORANGE(1),
    PLAY(2),
    T_MOBILE(3),
    PLUS_GSM(4);

    @Getter private final int value;

    Operator(int value) {
        this.value = value;
    }

    @Override public String toString() {
        return value + "";
    }
}
