package pl.simpay.api.model.directBilling.transaction;

import java.math.BigDecimal;

public record Values(BigDecimal net, BigDecimal gross, BigDecimal partner) {
}
