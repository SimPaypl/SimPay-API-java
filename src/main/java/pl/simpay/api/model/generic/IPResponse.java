package pl.simpay.api.model.generic;

import lombok.Data;

import java.util.List;

@Data
public class IPResponse {
    private String status;
    private List<String> ips;
}
