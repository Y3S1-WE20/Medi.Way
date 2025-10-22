package backend.service;

import backend.model.Doctor;
import backend.model.Appointment;
import backend.repository.DoctorRepository;
import backend.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class DoctorService {
    private final DoctorRepository repository;
    private final AppointmentRepository apptRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DoctorService(DoctorRepository repository, AppointmentRepository apptRepo) {
        this.repository = repository;
        this.apptRepo = apptRepo;
    }

    public Doctor create(String name, String email, String specialization, MultipartFile photo) throws IOException {
        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Doctor with this email already exists");
        }
        Doctor d = new Doctor();
        d.setName(name);
        d.setEmail(email);
        d.setSpecialization(specialization);
        if (photo != null && !photo.isEmpty()) {
            d.setPhoto(photo.getBytes());
            d.setPhotoContentType(photo.getContentType());
        }
        return repository.save(d);
    }

    public List<Doctor> list() {
        return repository.findAll();
    }

    public Doctor get(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
    }

    public Doctor update(Long id, String name, String email, String specialization, MultipartFile photo) throws IOException {
        Doctor d = get(id);
        if (email != null && !email.equals(d.getEmail()) && repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Doctor with this email already exists");
        }
        if (name != null) d.setName(name);
        if (email != null) d.setEmail(email);
        if (specialization != null) d.setSpecialization(specialization);
        if (photo != null) {
            if (!photo.isEmpty()) {
                d.setPhoto(photo.getBytes());
                d.setPhotoContentType(photo.getContentType());
            } else {
                d.setPhoto(null);
                d.setPhotoContentType(null);
            }
        }
        return repository.save(d);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public void setPassword(Long doctorId, String rawPassword) {
        Doctor d = get(doctorId);
        d.setPasswordHash(passwordEncoder.encode(rawPassword));
        repository.save(d);
    }

    public Doctor login(String email, String password) {
        Optional<Doctor> opt = repository.findByEmail(email);
        if (opt.isEmpty()) throw new IllegalArgumentException("Invalid credentials");
        Doctor d = opt.get();
        if (d.getPasswordHash()==null || !passwordEncoder.matches(password, d.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        // return safe copy
        Doctor copy = new Doctor();
        copy.setId(d.getId());
        copy.setName(d.getName());
        copy.setEmail(d.getEmail());
        copy.setSpecialization(d.getSpecialization());
        return copy;
    }

    public List<Appointment> appointmentsByDoctor(Long doctorId) {
        Doctor d = get(doctorId);
        return apptRepo.findByDoctor(d);
    }
}
