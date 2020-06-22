# java-simpay-api

## SMS
### Weryfikacja kodu
```java
Sms sms = new Sms();
Sms sms = new Sms("key","secret");

CodeVerifyRequest request = new CodeVerifyRequest();
request.setCode("code");
request.setKey("key"); // can be omitted  by passing value in constructor
request.setSecret("secret"); // can be omitted  by passing value in constructor
request.setNumber("number");
request.setService_id("service_id");

APIResponse<CodeVerifyResponse> response = sms.verifyCode(request);
List<String> error = response.getError(); // List of errors, if request was successful list will be empty
CodeVerifyResponse codeVerifyResponse = response.getRespond();
int from = codeVerifyResponse.getFrom(); // Sender number
int number = codeVerifyResponse.getNumber(); // Number where sms were sent
String status = codeVerifyResponse.getStatus(); // Status received from api
int test = codeVerifyResponse.getTest(); // 1 if sms was test else 0
double value = codeVerifyResponse.getValue(); // Code Value
```

### Pobieranie listy usług
```java
Sms sms = new Sms();
Sms sms = new Sms("key","secret");


ServiceListRequest request = new ServiceListRequest();
request.setKey("key");
request.setSecret("secret");
APIResponse<ServicesResponse> response = sms.getServiceList(request);
List<String> error = response.getError(); // List of errors, if request was successful list will be empty
ServicesResponse serviceList = response.getRespond();
String status = serviceList.getStatus(); // Status received from api
List<Service> services = serviceList.getServices(); // List of services
```

## SMS XML
```java
SmsXml smsXml = new SmsXml("apikey");
String code = smsXml.generateCode(); // Generate code
double number = smsXml.getSmsValue("number"); // retrieve information's about sms
String sms = smsXml.generateXml("sms"); // Generate xml from sms message
boolean ip = smsXml.getServersIp("ip"); // Check if passed ip is valid ip of simpay servers
```

## Direct Billing
### Generowanie transakcji
```java
DirectBilling directBilling = new DirectBilling();
DirectBilling directBilling = new DirectBilling("apiKey", "secret", false, 1);

DbGenerateRequest request = new DbGenerateRequest();
request.setAmount("amount");
request.setAmount_gross("amount_gross");
request.setAmount_required("amount_required");
request.setComplete("complete");
request.setFailure("failure");
request.setProvider(Operator.ORANGE); // orange, play, t-mobile, plus-gsm
request.setControl("control");
request.setServiceId(1);

DbGenerateResponse dbGenerateResponse = directBilling.generateTransaction(request);
dbGenerateResponse.getLink(); // Link
dbGenerateResponse.getName(); // Transaction Name
dbGenerateResponse.getStatus(); // Status received from api
```

### Pobieranie danych o transakcji
```java
DirectBilling directBilling = new DirectBilling();
DirectBilling directBilling = new DirectBilling("apiKey", "secret", false, 1);

DbTransactionRequest request = new DbTransactionRequest();
request.setId(1);
request.setKey("key"); // can be omitted  by passing value in constructor
request.setSecret("secret");  // can be omitted  by passing value in constructor

APIResponse<DbTransaction> response = directBilling.getTransaction(request);
List<String> error = response.getError(); // List of errors, if request was successful list will be empty
DbTransaction respond = response.getRespond();
```

### Pobieranie listy usług DCB
```java
DirectBilling directBilling = new DirectBilling();
DirectBilling directBilling = new DirectBilling("apiKey", "secret", false, 1);

DbServicesListRequest request = new DbServicesListRequest();
request.setApi("key"); // can be omitted  by passing value in constructor
request.setSecret("secret");  // can be omitted  by passing value in constructor

APIResponse<DbServicesListResponse> response = directBilling.getServices(request);
List<String> error = response.getError(); // List of errors, if request was successful list will be empty
DbTransaction respond = response.getRespond();
```

### Pobieranie maksymalnych kwot transakcji
```java
DirectBilling directBilling = new DirectBilling();
DirectBilling directBilling = new DirectBilling("apiKey", "secret", false, 1);

DbTransactionLimitsRequest request = new DbTransactionLimitsRequest();
request.setService_id(1);
request.setApi("key"); // can be omitted  by passing value in constructor
request.setSecret("secret");  // can be omitted  by passing value in constructor

APIResponse<List<DbTransactionLimit>> response = directBilling.getTransactionLimits(request);
```

### Pobieranie prowizji dla usługi
```java
DirectBilling directBilling = new DirectBilling();
DirectBilling directBilling = new DirectBilling("apiKey", "secret", false, 1);

DbServiceCommissionRequest request = new DbServiceCommissionRequest();
request.setService_id(1);
request.setApi("key"); // can be omitted  by passing value in constructor
request.setSecret("secret");  // can be omitted  by passing value in constructor

List<DbCommission> response = directBilling.getServiceCommission(request);
```

### Pobieranie adresów IP serwerów SimPay
```java
DirectBilling directBilling = new DirectBilling();

List<String> response = directBilling.getServersIp();
```

### Obliczanie podpisu sign
```java
DirectBilling directBilling = new DirectBilling();

String sign = directBilling.sign(int id, String status, String valuenet, String valuepartner, String control);
```