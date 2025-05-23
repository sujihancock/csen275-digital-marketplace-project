package handmade_goods.digital_marketplace.dto;

public class StripeResponse {
    private String status;
    private String message;
    private String sessionId;
    private String sessionUrl;

    public StripeResponse(String status, String message, String sessionId, String sessionUrl) {
        this.status = status;
        this.message = message;
        this.sessionId = sessionId;
        this.sessionUrl = sessionUrl;
    }

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getSessionId() { return sessionId; }
    public String getSessionUrl() { return sessionUrl; }

    public void setStatus(String status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setSessionUrl(String sessionUrl) { this.sessionUrl = sessionUrl; }
}
