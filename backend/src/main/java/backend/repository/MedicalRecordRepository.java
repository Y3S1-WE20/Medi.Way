package backend.repository;

import backend.model.MedicalRecord;
import backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientOrderByCreatedAtDesc(Patient patient);
}
