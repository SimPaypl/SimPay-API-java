package pl.simpay.api.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import lombok.SneakyThrows;
import okhttp3.*;
import pl.simpay.api.exceptions.APIException;

import java.lang.reflect.Type;

@Data
public class HttpService {
    private static final String CONTENT_TYPE_VALUE = "application/json";
    private static final int HTTP_OK_CODE = 200;

    private OkHttpClient init() {
        return new OkHttpClient();
    }

    private Gson gson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    @SneakyThrows public String sendPost(String url, RequestBody body) {
        Request.Builder builder = new Request.Builder();

        Request request = builder.url(url).post(body).build();

        try (Response response = init().newCall(request).execute()) {
            if (response.networkResponse().code() != HTTP_OK_CODE) {
                throw new APIException(response);
            }

            String x = response.body().string();

            System.out.println(x);

            return x;
        }
    }

    @SneakyThrows public String sendGet(String url) {
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).get().build();

        try (Response response = init().newCall(request).execute()) {
            if (response.networkResponse().code() != HTTP_OK_CODE) {
                throw new APIException(response);
            }

            return response.body().string();
        }
    }

    @SneakyThrows public String sendPost(String url, Object object) {
        return this.sendPost(url, RequestBody.create(MediaType.parse(CONTENT_TYPE_VALUE), gson().toJson(object)));
    }

    @SneakyThrows public <T> T sendPost(String url, RequestBody body, Class<T> clazz) {
        return gson().fromJson(this.sendPost(url, body), clazz);
    }

    @SneakyThrows public <T> T sendPost(String url, Object object, Class<T> clazz) {
        return gson().fromJson(this.sendPost(url, object), clazz);
    }

    @SneakyThrows public <T> T sendPost(String url, Object object, Type type) {
        return gson().fromJson(this.sendPost(url, object), type);
    }

    @SneakyThrows public <T> T sendGet(String url, Type type) {
        return gson().fromJson(this.sendGet(url), type);
    }
}
