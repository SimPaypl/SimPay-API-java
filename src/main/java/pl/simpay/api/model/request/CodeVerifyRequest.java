package pl.simpay.api.model.request;

public record CodeVerifyRequest(String code, long number) {
}
