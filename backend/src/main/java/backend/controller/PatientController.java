package backend.controller;

import backend.dto.LoginRequest;
import backend.dto.LoginResponse;
import backend.dto.RegisterRequest;
import backend.dto.RegisterResponse;
import backend.service.PatientService;
import javax.imageio.ImageIO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
public class PatientController {
    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        return service.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return service.login(request);
    }

    @GetMapping(value = "/{healthId}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQr(@PathVariable String healthId, @RequestParam(defaultValue = "false") boolean download) throws Exception {
        BufferedImage image = service.qrForHealthId(healthId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] bytes = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        if (download) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=healthid-" + healthId + ".png");
        }
        return ResponseEntity.ok().headers(headers).contentType(MediaType.IMAGE_PNG).body(bytes);
    }

    @GetMapping("/{healthId}")
    public Object getPatient(@PathVariable String healthId) {
        // expose minimal patient profile
        var opt = service.findByHealthId(healthId);
        return opt.map(p -> java.util.Map.of(
                "id", p.getId(),
                "fullName", p.getFullName(),
                "email", p.getEmail(),
                "healthId", p.getHealthId(),
                "phone", p.getPhone(),
                "address", p.getAddress(),
                "dateOfBirth", p.getDateOfBirth()
        )).orElseThrow(() -> new IllegalArgumentException("Patient not found"));
    }
}
