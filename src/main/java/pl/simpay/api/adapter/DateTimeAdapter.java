package pl.simpay.api.adapter;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class DateTimeAdapter implements Adapter<LocalDateTime> {

    @ToJson
    public String toJson(LocalDateTime localDateTime) {
        return localDateTime.toString();
    }

    @FromJson
    public LocalDateTime fromJson(String dateTime) {
        return ZonedDateTime.parse(dateTime).toLocalDateTime();
    }
}
