# Simpay API in Java 
API for one-time use SMS code, DirectBilling and SMS as XML.

## Used technology
- Java Spring (Web-services)
- Gradle
- JDK 1.8

## One-time use code
Example:
```java
    @Autowired        
    private SmsRequestService smsRequestService;

        SmsStatusResponse response = smsRequestService.getResponse("123", "7136", "sms-code");

        if (SimpayStatusType.OK.name().equals(response.getRespond().getStatus())) {

            System.out.println("The code is correct");
        }

        if (SimpayStatusType.USED.name().equals(response.getRespond().getStatus())) {

            System.out.println("The code has been used");
        }

        if (!CollectionUtils.isEmpty(response.getError())) {
            Error error = response.getError().get(0);

            System.out.println("Error code: " + error.getErrorCode());
            System.out.println("Error message: " + error.getErrorName());
            System.out.println("Error value: " + error.getErrorValue());
        }
```

## DirectBilling

Example of creating a payment url
```java
    public DbPaymentUrl createPaymentUrl(String serviceId, String control, AmountType amountType, String amountValue) throws IOException {
        List<NameValuePair> params = createParams(serviceId, control, amountType, amountValue);
        HttpResponse response = httpPostService.getResponse(DB_API_URL, params);

        return GsonUtil.convertToDbPaymentUrl(response.getEntity().getContent());
    }

    private List<NameValuePair> createParams(String serviceId, String control, AmountType amountType, String amountValue) {
        List<NameValuePair> params = new ArrayList<>();

        //Service ID from Simpay partner panel
        //Required: yes
        //Type: String
        params.add(new BasicNameValuePair("serviceId", serviceId));

        //Your own transaction ID
        //Required: yes
        //Type: String
        params.add(new BasicNameValuePair("control", control));

        //Type of amount which will be charged your customer
        //"amount" - net value of transaction (will be increased by 23% VAT)
        //"amount_gross" - gross value of transaction (with 23% VAT)
        //"amount_required" - real commission which you have to receive, independently of the mobile operator
        //amount value - "1.00" is 1,00 zł, 0.01 is 0,01 zł, 10.00 is 10,00 zł etc.
        //Required: yes, one of the above
        //Type: String
        params.add(new BasicNameValuePair(amountType.getType(), amountValue));

        //URL to redirect after success payment
        //Required: no
        //Type: String
        params.add(new BasicNameValuePair("complete", "https://your.domain.com/success"));

        //URL to redirect after unsuccessful payment
        //Required: no
        //Type: String
        params.add(new BasicNameValuePair("failure", "https://your.domain.com/failure"));

        //Set mobile operator
        //"1" – Orange,
        //"2" – Play,
        //"3" – T-Mobile,
        //"4" – Plus GSM
        //Required: no, by default Simpay will recognize the operator basing on the mobile phone number
        //Type: String
        params.add(new BasicNameValuePair("provider", "1"));

        Sha256Util.addDbPaymentControlSign(params, serviceId, amountValue, control);

        return params;
    }
```

Receiving response with payment status by POST request from Simpay
```java
@RestController
public class DbResponseController {

    private static final String CONFIRMATION_RESPONSE = "OK";

    @PostMapping(value = "/simpay_api")
    public String getResponse(@RequestParam MultiValueMap<String, String> responseData) {
        DbPaymentResponse dbPaymentResponse = PaymentResponseBuilder.buildPaymentResponse(responseData);

        if (dbPaymentResponseSignIsValid(dbPaymentResponse)
                && PaymentResponseStatus.ORDER_PAYED.equals(dbPaymentResponse.getStatus())) {

            System.out.println("Payment has been successfully completed");
        }

        if (dbPaymentResponseSignIsValid(dbPaymentResponse)
                && PaymentResponseStatus.ORDER_ACCEPTED.equals(dbPaymentResponse.getStatus())) {

            System.out.println("Order has been accepted - but not paid yet");
        }

        if (dbPaymentResponseSignIsValid(dbPaymentResponse)
                && PaymentResponseStatus.ORDER_REJECTED.equals(dbPaymentResponse.getStatus())) {

            System.out.println("Payment has been rejected");
        }

        return CONFIRMATION_RESPONSE;
    }
}
```

Check payment status by transactionId
```java
@Autowired
private DbService dbService;

PaymentStatusResponse status = dbService.getPaymentStatus(String transactionId);
```

Get all active DB services
```java
@Autowired
private DbService dbService;

DbActiveServices serviceList = dbService.getDbServices();
```

Get the maximum transaction value for the service
```java
@Autowired
private DbService dbService;

DbMaxTransactionValue maxValue = dbService.getDbMaxTransactionValue(String serviceId);
```

Get commission rate for the  service
```java
@Autowired
private DbService dbService;

DbServiceCommissionRate commissionRate = dbService.getDbServiceCommission(String serviceId);
```

## XML
With every transaction, Simpay will request by POST a XML response with generated text message for customer. 

```java
@RestController
public class XmlRequestController {

    private XmlResponseService xmlResponseService;

    public XmlRequestController(XmlResponseService xmlResponseService) {
        this.xmlResponseService = xmlResponseService;
    }

    @PostMapping(value = "/simpay_xml")
    public String getXmlRequest(@RequestParam MultiValueMap<String, String> requestData) {
        XmlRequest xmlRequest = XmlRequestBuilder.buildXmlRequest(requestData);
        XmlRequestStatus xmlRequestStatus = XmlRequestVerifier.getStatus(xmlRequest);

        return XmlRequestStatus.OK.equals(xmlRequestStatus)
                ? xmlResponseService.createMessage(xmlRequest)
                : xmlRequestStatus.name();
    }
}
```

