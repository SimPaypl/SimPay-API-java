package pl.simpay.api.service;

import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@RequiredArgsConstructor
class AuthInterceptor implements Interceptor {

    private static final String SIM_KEY_HEADER = "X-SIM-KEY";
    private static final String SIM_PASSWORD_HEADER = "X-SIM-PASSWORD";

    private final String apiKey;
    private final String simPassword;

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        var authRequest = chain.request().newBuilder().header(SIM_KEY_HEADER, apiKey).header(SIM_PASSWORD_HEADER, simPassword).build();
        return chain.proceed(authRequest);
    }
}
