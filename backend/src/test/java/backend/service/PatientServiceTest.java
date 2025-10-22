package backend.service;

import backend.dto.LoginRequest;
import backend.dto.RegisterRequest;
import backend.model.Patient;
import backend.repository.PatientRepository;
import com.google.zxing.WriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    private PatientRepository repo;
    private PatientService service;

    @BeforeEach
    void setUp(){
        repo = mock(PatientRepository.class);
        service = new PatientService(repo);
    }

    @Test
    void register_throws_on_existing_email(){
        RegisterRequest req = new RegisterRequest(); req.email = "a@b";
        when(repo.existsByEmail("a@b")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.register(req));
    }

    @Test
    void register_and_login_and_qr_and_findByHealthId() throws WriterException {
        RegisterRequest req = new RegisterRequest(); req.email = "u@v"; req.fullName = "F"; req.password = "p"; req.phone = "ph"; req.address = "addr"; req.dateOfBirth = LocalDate.of(1990,1,1);
        when(repo.existsByEmail("u@v")).thenReturn(false);
        Patient saved = new Patient(); saved.setId(22L); saved.setFullName("F"); saved.setEmail("u@v"); saved.setHealthId("HID");
        when(repo.save(any())).thenReturn(saved);
        var resp = service.register(req);
        assertEquals(22L, resp.id);

        // login
        Patient stored = new Patient(); stored.setPasswordHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("pw")); stored.setEmail("log@x");
        when(repo.findByEmail("log@x")).thenReturn(Optional.of(stored));
        LoginRequest lr = new LoginRequest(); lr.email = "log@x"; lr.password = "pw";
        var lres = service.login(lr);
        assertEquals("Login successful", lres.message);

        // qr generation returns image
        BufferedImage img = service.qrForHealthId("anything");
        assertNotNull(img);

        when(repo.findByHealthId("HID")).thenReturn(Optional.of(saved));
    assertTrue(service.findByHealthId("HID").isPresent());
    }
}
