package pl.simpay.api.type.db.service;

import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;
import pl.simpay.api.type.ParametersWrapper;
import pl.simpay.api.type.db.domain.dbmaxtransactionvalue.DbMaxTransactionValueRequest;
import pl.simpay.api.type.db.domain.dbmaxtransactionvalue.DbMaxTransactionValue;
import pl.simpay.api.type.db.domain.dbservicecommission.DbServiceCommissionRequest;
import pl.simpay.api.type.db.domain.dbservicecommission.DbServiceCommissionRate;
import pl.simpay.api.type.db.domain.dbservicelist.DbServiceListRequest;
import pl.simpay.api.type.db.domain.dbservicelist.DbActiveServices;
import pl.simpay.api.type.db.domain.paymentinformation.PaymentStatusRequest;
import pl.simpay.api.type.db.domain.paymentinformation.PaymentStatusResponse;
import pl.simpay.api.util.GsonUtil;
import pl.simpay.api.util.HttpPostService;

import java.io.IOException;

import static pl.simpay.config.ApiParameters.*;

@Service
public class DbService {

    private HttpPostService httpPostService;

    public DbService(HttpPostService httpPostService) {
        this.httpPostService = httpPostService;
    }

    public PaymentStatusResponse getPaymentStatus(String transactionId) throws IOException {
        HttpResponse response = getResponse(new ParametersWrapper(new PaymentStatusRequest(transactionId)), DB_STATUS_API_URL);

        return GsonUtil.convertToPaymentStatusResponse(response.getEntity().getContent());
    }

    public DbActiveServices getDbServices() throws IOException {
        HttpResponse response = getResponse(new ParametersWrapper(new DbServiceListRequest()), DB_SERVICES_LIST_URL);

        return GsonUtil.convertToDbServiceListResponse(response.getEntity().getContent());
    }

    public DbMaxTransactionValue getDbMaxTransactionValue(String serviceId) throws IOException {
        HttpResponse response = getResponse(new ParametersWrapper(new DbMaxTransactionValueRequest(serviceId)), DB_MAX_TRANSACTION_VALUE_URL);

        return GsonUtil.convertToDbMaxTransactionValueResponse(response.getEntity().getContent());
    }

    public DbServiceCommissionRate getDbServiceCommission(String serviceId) throws IOException {
        HttpResponse response = getResponse(new ParametersWrapper(new DbServiceCommissionRequest(serviceId)), DB_SERVICE_COMMISSION_URL);

        return GsonUtil.convertToDbServiceCommissionResponse(response.getEntity().getContent());
    }

    private HttpResponse getResponse(ParametersWrapper parameters, String url) throws IOException {
        return httpPostService.getResponseWithEntityAsJson(url, GsonUtil.toJson(parameters));
    }
}
