package pl.simpay.api.model.payment;

import com.squareup.moshi.Json;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Compact namespace for Payment API request and response records. */
public final class PaymentModels {
    private PaymentModels() {}

    public enum BlikTicketType { T6 }
    public enum BlikModel { A, O, M }
    public enum AliasType { PAYID, UID }
    public enum AliasStatus { @Json(name="alias_pending_registration") PENDING_REGISTRATION, @Json(name="alias_active") ACTIVE, @Json(name="alias_expired") EXPIRED, @Json(name="alias_unregistered") UNREGISTERED }
    public enum SubscriptionMode { @Json(name="BLIK") BLIK, @Json(name="CARD") CARD }
    public enum SubscriptionStatus { @Json(name="subscription_pending") PENDING, @Json(name="subscription_active") ACTIVE, @Json(name="subscription_cancelled") CANCELLED, @Json(name="subscription_expired") EXPIRED, @Json(name="subscription_finished") FINISHED, @Json(name="subscription_fraudulent") FRAUDULENT }
    public enum SubscriptionCancelledBy { @Json(name="system") SYSTEM, @Json(name="antifraud") ANTIFRAUD, @Json(name="merchant") MERCHANT, @Json(name="payer") PAYER, @Json(name="blik") BLIK }
    public enum TransactionStatus { @Json(name="transaction_new") NEW, @Json(name="transaction_generated") GENERATED, @Json(name="transaction_confirmed") CONFIRMED, @Json(name="transaction_paid") PAID, @Json(name="transaction_failure") FAILURE, @Json(name="transaction_expired") EXPIRED, @Json(name="transaction_refunded") REFUNDED, @Json(name="transaction_fraud") FRAUD, @Json(name="transaction_fraud_possibility") FRAUD_POSSIBILITY }

    public record Customer(String name, String email, String ip, String countryCode) {}
    public record Address(String name, String surname, String street, String building, String flat, String city, String region, String postalCode, String country, String company) {}
    public record CartItem(String name, Integer quantity, BigDecimal price, String producer, String category, String code) {}
    public record AntiFraud(@Json(name="useragent") String userAgent, @Json(name="steamid") String steamId64, @Json(name="mcusername") String minecraftUsername, @Json(name="mcid") String minecraftUuid, String systemId) {}
    public record ChannelTypes(boolean blik, boolean transfer, boolean cards, boolean ewallets, boolean paypal, boolean paysafe, boolean latam, boolean postponed) {
        public ChannelTypes() { this(true, true, true, true, true, true, true, false); }
    }
    public record Returns(String success, String failure) {}
    public record GenerateTransactionRequest(BigDecimal amount, String currency, String description, String control, Customer customer, @Json(name="antifraud") AntiFraud antiFraud, Address billing, Address shipping, List<CartItem> cart, Returns returns, String directChannel, List<String> channels, ChannelTypes channelTypes, String referer) {
        public GenerateTransactionRequest {
            Objects.requireNonNull(amount, "amount");
            if (amount.signum() <= 0) throw new IllegalArgumentException("amount must be positive");
            currency = currency == null ? "PLN" : currency;
            cart = cart == null ? null : List.copyOf(cart);
            channels = channels == null ? null : List.copyOf(channels);
        }
        public GenerateTransactionRequest(BigDecimal amount) { this(amount, "PLN", null, null, null, null, null, null, null, null, null, null, null, null); }
    }
    public record TransactionGenerated(String redirectUrl, String transactionId) {}
    public record TransactionCustomer(String name, String email, String ip, String country) {}
    public record Amount(BigDecimal value, String currency, BigDecimal commission, @Json(name="commission_currency") String commissionCurrency, OriginalAmount original) {}
    public record OriginalAmount(BigDecimal value, String currency, @Json(name="pln_rate") String plnRate) {}
    public record TransactionInfo(String id, TransactionStatus status, Amount amount, String channel, String control, String description, @Json(name="redirect") Returns redirects, TransactionCustomer customer, Address billing, Address shipping, List<CartItem> cart, Map<String,Object> additional, @Json(name="paid_at") OffsetDateTime paidAt, @Json(name="expires_at") OffsetDateTime expiresAt, @Json(name="created_at") OffsetDateTime createdAt, @Json(name="updated_at") OffsetDateTime updatedAt, @Json(name="payer_transaction_id") String payerTransactionId) {}
    public record ChannelAmount(BigDecimal min, BigDecimal max) {}
    public record Channel(String id, String name, String type, String img, BigDecimal commission, List<String> currencies, ChannelAmount amount) {}
    public record Currency(String iso, @Json(name="pln_rate") String plnRate, @Json(name="nbp_table") String nbpTable, String prefix, String suffix, @Json(name="updated_at") OffsetDateTime updatedAt) {}
    public record BlikLevel0Request(Map<String,String> ticket) { public BlikLevel0Request(BlikTicketType type, String ticket) { this(Map.of(type.name(), Objects.requireNonNull(ticket))); } }
    public record BlikTicket(@Json(name="T6") String ticket) { public BlikTicket { Objects.requireNonNull(ticket); } }
    public record BlikAlias(String value, AliasType type, String label) { public BlikAlias { Objects.requireNonNull(value); type = type == null ? AliasType.PAYID : type; } }
    public record BlikSubscriptionOptions(BlikModel model, LocalDate expiresAt, String frequency, BigDecimal amountLimitPerTransaction, LocalDate initiationDate, BigDecimal amountLimitTotal, Integer transactionsCountLimit) {
        public BlikSubscriptionOptions {
            Objects.requireNonNull(model, "model");
            if (model == BlikModel.A && (expiresAt == null || frequency == null || amountLimitPerTransaction == null || initiationDate == null || amountLimitTotal == null)) throw new IllegalArgumentException("model A requires expiresAt, frequency, amountLimitPerTransaction, initiationDate and amountLimitTotal");
            if (model == BlikModel.O && (frequency != null || amountLimitPerTransaction != null || amountLimitTotal != null || transactionsCountLimit != null)) throw new IllegalArgumentException("model O prohibits frequency and amount/count limits");
            if (model == BlikModel.M && (amountLimitPerTransaction != null || amountLimitTotal != null || transactionsCountLimit != null)) throw new IllegalArgumentException("model M prohibits amount/count limits");
        }
    }
    public record CreateSubscriptionRequest(String transactionId, BlikTicket ticket, BlikAlias alias, BlikSubscriptionOptions options, Map<String,String> descriptions, String appUrl) { public CreateSubscriptionRequest { Objects.requireNonNull(transactionId); Objects.requireNonNull(ticket); Objects.requireNonNull(alias); Objects.requireNonNull(options); descriptions = descriptions == null ? null : Map.copyOf(descriptions); } }
    public record SubscriptionCreated(String subscriptionId, String aliasId) {}
    public record AutoPaymentAlias(String label, Boolean noDelay) {}
    public record AutoPaymentRequest(String transactionId, Map<String,String> descriptions, Integer attempt, AutoPaymentAlias alias) { public AutoPaymentRequest { Objects.requireNonNull(transactionId); descriptions = descriptions == null ? null : Map.copyOf(descriptions); } }
    public record AutoPaymentResult(boolean needsUserConfirmation) {}
    public record SubscriptionBlik(BlikModel model, Alias alias) {}
    public record Cancelled(SubscriptionCancelledBy by, String reason) {}
    public record Subscription(String id, SubscriptionStatus status, SubscriptionMode mode, SubscriptionBlik blik, String frequency, @Json(name="initiation_date") LocalDate initiationDate, @Json(name="total_amount_limit") BigDecimal totalAmountLimit, @Json(name="total_transactions_limit") Integer totalTransactionsLimit, Cancelled cancelled, @Json(name="created_at") OffsetDateTime createdAt, @Json(name="updated_at") OffsetDateTime updatedAt) {}
    public record Alias(String id, AliasType type, String value, String label, @Json(name="blik_key") String blikKey, AliasStatus status, @Json(name="expires_at") OffsetDateTime expiresAt, @Json(name="created_at") OffsetDateTime createdAt, @Json(name="updated_at") OffsetDateTime updatedAt, Subscription subscription) {}
    public record ListResponse<T>(List<T> data, pl.simpay.api.model.pagination.Pagination pagination) {}
    public record SubscriptionList(List<Subscription> data, pl.simpay.api.model.pagination.Pagination pagination) {}
    public record AliasList(List<Alias> data, pl.simpay.api.model.pagination.Pagination pagination) {}
    public record SubscriptionQuery(int page, int perPage, String uuid, SubscriptionStatus status, SubscriptionMode mode, String sort) { public SubscriptionQuery { positive(page, perPage); } public SubscriptionQuery() { this(1,20,null,null,null,null); } }
    public record AliasQuery(int page, int perPage, AliasStatus status, AliasType type, String uuid, String value, String sort) { public AliasQuery { positive(page, perPage); } public AliasQuery() { this(1,20,null,null,null,null,null); } }
    public record UnregisterAliasRequest(AliasType type, String uuid, String value, String reason) { public UnregisterAliasRequest { if (uuid == null && (type == null || value == null)) throw new IllegalArgumentException("provide uuid or value with type"); } }

    public static Map<String,String> descriptions(String text) {
        Objects.requireNonNull(text);
        var result = new java.util.LinkedHashMap<String,String>();
        int count = text.codePointCount(0, text.length());
        for (int line = 0; line < 3 && line * 35 < count; line++) {
            int from = text.offsetByCodePoints(0, line * 35), to = text.offsetByCodePoints(0, Math.min(count, (line + 1) * 35));
            result.put("line" + (line + 1), text.substring(from, to));
        }
        return Map.copyOf(result);
    }
    private static void positive(int page, int perPage) { if (page <= 0 || perPage <= 0) throw new IllegalArgumentException("page and perPage must be positive"); }
}
