package backend.repository;

import backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByHealthId(String healthId);
    Optional<Patient> findByHealthId(String healthId);
}
