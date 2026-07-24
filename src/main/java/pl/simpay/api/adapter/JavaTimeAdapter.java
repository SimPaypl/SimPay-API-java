package pl.simpay.api.adapter;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public final class JavaTimeAdapter {
    @ToJson public String localDate(LocalDate value) { return value.toString(); }
    @FromJson public LocalDate localDate(String value) { return LocalDate.parse(value); }
    @ToJson public String offsetDateTime(OffsetDateTime value) { return value.toString(); }
    @FromJson public OffsetDateTime offsetDateTime(String value) { return OffsetDateTime.parse(value); }
}
