package backend.service;

import backend.model.Doctor;
import backend.model.MedicalRecord;
import backend.model.Patient;
import backend.repository.DoctorRepository;
import backend.repository.MedicalRecordRepository;
import backend.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MedicalRecordService {
    private final MedicalRecordRepository repository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public MedicalRecordService(MedicalRecordRepository repository, PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.repository = repository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public MedicalRecord addRecord(Long doctorId, String patientHealthId, String diagnosis, String prescriptions, String labNotes, String comments) {
        Patient patient = patientRepository.findAll().stream()
                .filter(p -> patientHealthId.equals(p.getHealthId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        MedicalRecord rec = new MedicalRecord();
        rec.setPatient(patient);
        rec.setDoctor(doctor);
        rec.setDiagnosis(diagnosis);
        rec.setPrescriptions(prescriptions);
        rec.setLabNotes(labNotes);
        rec.setComments(comments);
        return repository.save(rec);
    }

    public List<Map<String,Object>> listForPatient(String healthId){
        Patient patient = patientRepository.findAll().stream()
                .filter(p -> healthId.equals(p.getHealthId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        return repository.findByPatientOrderByCreatedAtDesc(patient).stream().map(r -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id", r.getId());
            m.put("createdAt", r.getCreatedAt());
            m.put("diagnosis", r.getDiagnosis());
            m.put("prescriptions", r.getPrescriptions());
            m.put("labNotes", r.getLabNotes());
            m.put("comments", r.getComments());
            Map<String,Object> doc = new HashMap<>();
            doc.put("id", r.getDoctor().getId());
            doc.put("name", r.getDoctor().getName());
            doc.put("email", r.getDoctor().getEmail());
            doc.put("specialization", r.getDoctor().getSpecialization());
            m.put("doctor", doc);
            return m;
        }).toList();
    }
}
