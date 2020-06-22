package pl.simpay.api.model.db.responses;

import lombok.Data;

@Data
public class DbGenerateResponse {
    private String status;
    private String link;
    private String name;
}
