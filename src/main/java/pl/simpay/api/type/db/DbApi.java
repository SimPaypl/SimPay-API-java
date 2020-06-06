package pl.simpay.api.type.db;

import org.springframework.stereotype.Service;
import pl.simpay.api.type.db.domain.DbPaymentUrl;
import pl.simpay.api.type.db.service.DbPaymentUrlService;
import pl.simpay.api.type.db.type.AmountType;
import pl.simpay.api.type.db.type.PaymentRequestStatus;

import java.io.IOException;

@Service
public class DbApi {

    private DbPaymentUrlService dbPaymentUrlService;

    public DbApi(DbPaymentUrlService dbPaymentUrlService) {
        this.dbPaymentUrlService = dbPaymentUrlService;
    }

    public void getPaymentLink() throws IOException {
        DbPaymentUrl dbPaymentUrl = dbPaymentUrlService.createPaymentUrl("128", "123", AmountType.AMOUNT, "1.00");

        if (PaymentRequestStatus.SUCCESS.getStatus().equals(dbPaymentUrl.getStatus())) {
            System.out.println("Payment link: " + dbPaymentUrl.getLink());
        }

        if (PaymentRequestStatus.FAILURE.getStatus().equals(dbPaymentUrl.getStatus())) {
            System.out.println("Error message: " + dbPaymentUrl.getMessage());
        }
    }
}
