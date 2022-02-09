package pl.simpay.api.adapter;

public interface Adapter<T> {

    T fromJson(String json);

    String toJson(T type);
}
