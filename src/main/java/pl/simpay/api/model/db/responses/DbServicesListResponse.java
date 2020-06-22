package pl.simpay.api.model.db.responses;

import lombok.Data;
import pl.simpay.api.model.db.DbService;

import java.util.List;

@Data
public class DbServicesListResponse {
    private String status;
    private List<DbService> services;
}
