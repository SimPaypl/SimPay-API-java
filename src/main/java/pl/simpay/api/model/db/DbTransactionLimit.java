package pl.simpay.api.model.db;

import lombok.Data;

@Data
public class DbTransactionLimit {
    private int id;
    private String name;
    private String max;
}
