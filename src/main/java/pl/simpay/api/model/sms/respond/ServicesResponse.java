package pl.simpay.api.model.sms.respond;

import lombok.Data;

import java.util.List;

@Data
public class ServicesResponse {
    private String status;
    private List<Service> services;
}
