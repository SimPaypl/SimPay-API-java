package pl.simpay.api.payments;

import com.squareup.moshi.Types;
import pl.simpay.api.model.payment.PaymentModels;
import pl.simpay.api.model.response.Response;
import pl.simpay.api.service.RestService;
import pl.simpay.api.util.Sha256;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Current bearer-authenticated Payment API client, bound to one service. */
public final class Payment {
    private final RestService rest;
    private final String serviceId;
    private final String ipnHash;

    public Payment(RestService rest, String serviceId, String ipnHash) {
        this.rest = Objects.requireNonNull(rest);
        this.serviceId = Objects.requireNonNull(serviceId);
        this.ipnHash = ipnHash;
    }

    public PaymentModels.TransactionGenerated generateTransaction(PaymentModels.GenerateTransactionRequest request) { return post("/transactions", request, PaymentModels.GenerateTransactionRequest.class, PaymentModels.TransactionGenerated.class); }
    public PaymentModels.TransactionInfo transactionInfo(String transactionId) { return get("/transactions/" + segment(transactionId), PaymentModels.TransactionInfo.class); }
    public List<PaymentModels.Channel> channels() { return getList("/channels", PaymentModels.Channel.class); }
    public List<PaymentModels.Currency> currencies() { return getList("/currencies", PaymentModels.Currency.class); }
    public boolean initiateBlikLevel0(String transactionId, String ticket) {
        rest.sendPostRequest(path("/blik/level0/" + segment(transactionId)), new PaymentModels.BlikLevel0Request(PaymentModels.BlikTicketType.T6, ticket), PaymentModels.BlikLevel0Request.class, responseType(Object.class));
        return true;
    }
    public PaymentModels.SubscriptionList subscriptions(PaymentModels.SubscriptionQuery query) {
        var q = new LinkedHashMap<String,String>(); q.put("page", String.valueOf(query.page())); q.put("perPage", String.valueOf(query.perPage()));
        put(q,"filter[uuid]",query.uuid()); put(q,"filter[status]", enumJson(query.status())); put(q,"filter[mode]",enumJson(query.mode())); put(q,"sort",query.sort());
        return cast(rest.sendGetRequest(path("/subscriptions"), q, PaymentModels.SubscriptionList.class));
    }
    public PaymentModels.SubscriptionCreated createBlikSubscription(PaymentModels.CreateSubscriptionRequest request) { return post("/blik/subscriptions", request, PaymentModels.CreateSubscriptionRequest.class, PaymentModels.SubscriptionCreated.class); }
    public PaymentModels.AutoPaymentResult blikAutoPayment(String subscriptionId, PaymentModels.AutoPaymentRequest request) { return post("/blik/subscriptions/" + segment(subscriptionId) + "/autopayment", request, PaymentModels.AutoPaymentRequest.class, PaymentModels.AutoPaymentResult.class); }
    public PaymentModels.AliasList blikAliases(PaymentModels.AliasQuery query) {
        var q = new LinkedHashMap<String,String>(); q.put("page", String.valueOf(query.page())); q.put("perPage", String.valueOf(query.perPage()));
        put(q,"filter[status]",enumJson(query.status())); put(q,"filter[type]",enumJson(query.type())); put(q,"filter[uuid]",query.uuid()); put(q,"filter[value]",query.value()); put(q,"sort",query.sort());
        return cast(rest.sendGetRequest(path("/blik/aliases"), q, PaymentModels.AliasList.class));
    }
    public boolean unregisterBlikAlias(PaymentModels.UnregisterAliasRequest request) {
        rest.sendDeleteRequest(path("/blik/aliases"), request, PaymentModels.UnregisterAliasRequest.class, responseType(Object.class)); return true;
    }

    /** Validates flattened IPN values in map/list insertion order, excluding signature. */
    public boolean ipnSignatureValid(Map<String, ?> payload) {
        if (ipnHash == null || payload == null || !(payload.get("signature") instanceof String signature) || !signature.matches("(?i)[0-9a-f]{64}")) return false;
        var values = new java.util.ArrayList<String>();
        payload.forEach((key, value) -> { if (!"signature".equals(key)) flatten(value, values); });
        values.add(ipnHash);
        return MessageDigest.isEqual(HexFormat.of().parseHex(Sha256.hash(String.join("|", values))), HexFormat.of().parseHex(signature));
    }

    private <T> T get(String suffix, Class<T> type) { return data(rest.sendGetRequest(path(suffix), responseType(type))); }
    private <T> List<T> getList(String suffix, Class<T> type) { Type list = Types.newParameterizedType(List.class, type); return data(rest.sendGetRequest(path(suffix), Types.newParameterizedType(Response.class, list))); }
    private <Q,T> T post(String suffix, Q request, Class<Q> requestType, Class<T> type) { return data(rest.sendPostRequest(path(suffix), request, requestType, responseType(type))); }
    private Type responseType(Type type) { return Types.newParameterizedType(Response.class, type); }
    private String path(String suffix) { return "/payment/" + segment(serviceId) + suffix; }
    private static String segment(String value) { Objects.requireNonNull(value); if (value.contains("/") || value.contains("?") || value.contains("#")) throw new IllegalArgumentException("invalid path identifier"); return value; }
    private static void put(Map<String,String> map, String key, String value) { if (value != null) map.put(key,value); }
    private static String enumJson(Enum<?> value) { if (value == null) return null; try { var field=value.getDeclaringClass().getField(value.name()); var json=field.getAnnotation(com.squareup.moshi.Json.class); return json == null ? value.name() : json.name(); } catch (NoSuchFieldException e) { throw new IllegalStateException(e); } }
    private static void flatten(Object value, List<String> output) { if (value instanceof Map<?,?> map) map.values().forEach(v -> flatten(v,output)); else if (value instanceof Iterable<?> list) list.forEach(v -> flatten(v,output)); else if (value != null) output.add(String.valueOf(value)); }
    @SuppressWarnings("unchecked") private static <T> T cast(Object value) { return (T)value; }
    private static <T> T data(Object value) { return Payment.<Response<T>>cast(value).data(); }
}
