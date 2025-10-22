package backend.dto;

public class RegisterResponse {
    public Long id;
    public String fullName;
    public String email;
    public String healthId;

    public RegisterResponse(Long id, String fullName, String email, String healthId) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.healthId = healthId;
    }
}
