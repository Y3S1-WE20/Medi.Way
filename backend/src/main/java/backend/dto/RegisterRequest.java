package backend.dto;

import java.time.LocalDate;

public class RegisterRequest {
    public String fullName;
    public String email;
    public String password;
    public String phone;
    public String address;
    public LocalDate dateOfBirth;
}
