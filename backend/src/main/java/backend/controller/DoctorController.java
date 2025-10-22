package backend.controller;

import backend.model.Doctor;
import backend.model.Appointment;
import backend.service.DoctorService;
import backend.dto.LoginRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = {"http://localhost:3000"})
public class DoctorController {
    private final DoctorService service;

    public DoctorController(DoctorService service) {
        this.service = service;
    }

    // Admin: create doctor profile with photo upload
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Doctor create(@RequestPart("name") String name,
                         @RequestPart("email") String email,
                         @RequestPart("specialization") String specialization,
                         @RequestPart(value = "photo", required = false) MultipartFile photo) throws Exception {
        return service.create(name, email, specialization, photo);
    }

    // Public: list doctors (without photo bytes)
    @GetMapping
    public List<Doctor> list() {
        return service.list().stream().map(d -> {
            Doctor copy = new Doctor();
            copy.setId(d.getId());
            copy.setName(d.getName());
            copy.setEmail(d.getEmail());
            copy.setSpecialization(d.getSpecialization());
            // exclude photo data here
            return copy;
        }).toList();
    }

    // Public: get doctor detail (without photo bytes)
    @GetMapping("/{id}")
    public Doctor get(@PathVariable Long id) {
        Doctor d = service.get(id);
        Doctor copy = new Doctor();
        copy.setId(d.getId());
        copy.setName(d.getName());
        copy.setEmail(d.getEmail());
        copy.setSpecialization(d.getSpecialization());
        return copy;
    }

    // Public: get doctor photo by id
    @GetMapping(value = "/{id}/photo")
    public ResponseEntity<byte[]> photo(@PathVariable Long id) {
        Doctor d = service.get(id);
        byte[] bytes = d.getPhoto();
        String ct = d.getPhotoContentType() != null ? d.getPhotoContentType() : MediaType.IMAGE_JPEG_VALUE;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, ct)
                .body(bytes != null ? bytes : new byte[0]);
    }

    // Admin: update doctor (multipart - all fields optional)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Doctor update(@PathVariable Long id,
                         @RequestPart(value = "name", required = false) String name,
                         @RequestPart(value = "email", required = false) String email,
                         @RequestPart(value = "specialization", required = false) String specialization,
                         @RequestPart(value = "photo", required = false) MultipartFile photo) throws Exception {
        return service.update(id, name, email, specialization, photo);
    }

    // Admin: delete doctor
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // Admin: set/update password for doctor account
    @PostMapping("/{id}/password")
    public void setPassword(@PathVariable Long id, @RequestParam String password){
        service.setPassword(id, password);
    }

    // Doctor: login with email/password (email must match created doctor)
    @PostMapping("/login")
    public Doctor login(@RequestBody LoginRequest req){
        return service.login(req.email, req.password);
    }

    // Doctor: my appointments by doctor id
    @GetMapping("/{id}/appointments")
    public List<Appointment> myAppointments(@PathVariable Long id){
        return service.appointmentsByDoctor(id);
    }
}
