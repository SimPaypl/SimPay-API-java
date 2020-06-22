package pl.simpay.api.model.sms.respond;

import lombok.Data;

@Data
public class CodeVerifyResponse {
    private String status;
    private int test;
    private int from;
    private int number;
    private String code;
    private long time_used;
    private double value;
}
