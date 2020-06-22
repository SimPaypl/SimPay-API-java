package pl.simpay.api.model.sms.respond;

import lombok.Data;

import java.util.List;

@Data
public class Service {
    private int id;
    private String suffix;
    private List<String> numbers;
}
