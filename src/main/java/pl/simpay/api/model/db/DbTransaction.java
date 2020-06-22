package pl.simpay.api.model.db;

import lombok.Data;

@Data
public class DbTransaction {
    private int id;
    private double valuenet;
    private double valuenet_gross;
    private double valuenet_partner;
    private String control;
    private String number_from;
    private String sign;
    private String status;
}
