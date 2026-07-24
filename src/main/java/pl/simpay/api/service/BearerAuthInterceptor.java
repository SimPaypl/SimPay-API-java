package pl.simpay.api.service;

import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

final class BearerAuthInterceptor implements Interceptor {
    private final String token;

    BearerAuthInterceptor(String token) {
        this.token = Objects.requireNonNull(token, "token");
    }

    @Override public @NotNull Response intercept(@NotNull Chain chain) throws IOException {
        var request = chain.request().newBuilder()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("X-SIM-VERSION", "3.0.0")
                .header("X-SIM-PLATFORM", "JAVA")
                .build();
        return chain.proceed(request);
    }
}
