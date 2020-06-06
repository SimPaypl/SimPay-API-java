package pl.simpay.api.type.xml.domain;

public class XmlRequest {
    private String id;
    private String smsFrom;
    private String sendNumber;
    private String smsText;
    private String sendTime;
    private String sign;

    public XmlRequest(String id, String smsFrom, String sendNumber, String smsText, String sendTime, String sign) {
        this.id = id;
        this.smsFrom = smsFrom;
        this.sendNumber = sendNumber;
        this.smsText = smsText;
        this.sendTime = sendTime;
        this.sign = sign;
    }

    public String getId() {
        return id;
    }

    public String getSmsFrom() {
        return smsFrom;
    }

    public String getSendNumber() {
        return sendNumber;
    }

    public String getSmsText() {
        return smsText;
    }

    public String getSendTime() {
        return sendTime;
    }

    public String getSign() {
        return sign;
    }
}
