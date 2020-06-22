package pl.simpay.api.model.sms.respond;

import lombok.Data;

import java.util.List;

@Data
public class Service {
    private int id;
    private String sufix;
    private List<String> numbers;
    private String status;
}
