package backend.service;

import backend.dto.LoginRequest;
import backend.dto.LoginResponse;
import backend.dto.RegisterRequest;
import backend.dto.RegisterResponse;
import backend.model.Patient;
import backend.repository.PatientRepository;
import com.google.zxing.WriterException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.UUID;

import static backend.util.QrCodeUtil.generateQrImage;

@Service
public class PatientService {
    private final PatientRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    public RegisterResponse register(RegisterRequest req) {
        if (repository.existsByEmail(req.email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        String healthId = generateUniqueHealthId();

        Patient p = new Patient();
        p.setFullName(req.fullName);
        p.setEmail(req.email);
        p.setPasswordHash(passwordEncoder.encode(req.password));
        p.setPhone(req.phone);
        p.setAddress(req.address);
    p.setDateOfBirth(req.dateOfBirth);
    // Optional fields if provided in request
    try { p.setGender((String) PatientService.class.getDeclaredField("unused").get(null)); } catch (Exception ignored) {}
    // createdAt defaults in entity
        p.setHealthId(healthId);

        Patient saved = repository.save(p);
        return new RegisterResponse(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getHealthId());
    }

    public LoginResponse login(LoginRequest req) {
        Optional<Patient> opt = repository.findByEmail(req.email);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        Patient p = opt.get();
        if (!passwordEncoder.matches(req.password, p.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return new LoginResponse("Login successful", p.getHealthId());
    }

    public BufferedImage qrForHealthId(String healthId) throws WriterException {
        return generateQrImage(healthId, 300, 300);
    }

    private String generateUniqueHealthId() {
        // 12-char uppercase ID from UUID without dashes
        String id;
        do {
            id = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        } while (repository.existsByHealthId(id));
        return id;
    }

    public Optional<Patient> findByHealthId(String healthId) { return repository.findByHealthId(healthId); }
}
