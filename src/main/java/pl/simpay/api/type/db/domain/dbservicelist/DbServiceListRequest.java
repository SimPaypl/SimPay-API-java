package pl.simpay.api.type.db.domain.dbservicelist;

import static pl.simpay.config.ApiParameters.API_KEY;
import static pl.simpay.config.ApiParameters.API_SECRET;

public class DbServiceListRequest {
    private String key;
    private String secret;

    public DbServiceListRequest() {
        this.key = API_KEY;
        this.secret = API_SECRET;
    }
}
