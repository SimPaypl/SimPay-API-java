package pl.simpay.api.model.directBilling.transaction;

/** Return URLs included in a Direct Billing transaction notification. */
public record DirectBillingNotificationReturns(String complete, String failure) {
}
