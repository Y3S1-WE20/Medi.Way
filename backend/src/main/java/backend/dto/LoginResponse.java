package backend.dto;

public class LoginResponse {
    public String message;
    public String healthId;

    public LoginResponse(String message, String healthId) {
        this.message = message;
        this.healthId = healthId;
    }
}
