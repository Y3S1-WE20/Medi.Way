package backend.service;

import backend.model.Doctor;
import backend.model.MedicalRecord;
import backend.model.Patient;
import backend.repository.DoctorRepository;
import backend.repository.MedicalRecordRepository;
import backend.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MedicalRecordServiceTest {

    private MedicalRecordRepository repository;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private MedicalRecordService service;

    @BeforeEach
    void setUp() {
        repository = mock(MedicalRecordRepository.class);
        patientRepository = mock(PatientRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        service = new MedicalRecordService(repository, patientRepository, doctorRepository);
    }

    @Test
    void addRecord_success_savesAndReturns() {
        Long doctorId = 2L;
        String healthId = "h-1";

        Patient patient = new Patient();
        patient.setId(10L);
        patient.setHealthId(healthId);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setName("Dr Test");
        doctor.setEmail("dr@test.com");
        doctor.setSpecialization("Cardiology");

        when(patientRepository.findAll()).thenReturn(List.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        MedicalRecord saved = new MedicalRecord();
        saved.setId(77L);
        when(repository.save(any(MedicalRecord.class))).thenReturn(saved);

        MedicalRecord result = service.addRecord(doctorId, healthId, "diag", "rx", "labs", "comments");
        assertNotNull(result);
        assertEquals(77L, result.getId());

        verify(repository).save(any(MedicalRecord.class));
    }

    @Test
    void addRecord_throws_whenPatientNotFound() {
        when(patientRepository.findAll()).thenReturn(List.of());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addRecord(1L, "nope", "d", "p", "l", "c"));
        assertEquals("Patient not found", ex.getMessage());
        verifyNoInteractions(doctorRepository);
        verifyNoInteractions(repository);
    }

    @Test
    void addRecord_throws_whenDoctorNotFound() {
        Patient p = new Patient();
        p.setHealthId("h-2");
        when(patientRepository.findAll()).thenReturn(List.of(p));
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.addRecord(99L, "h-2", "d", "p", "l", "c"));
        assertEquals("Doctor not found", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void listForPatient_mapsFieldsAndDoctorInfo() {
        String healthId = "h-3";
        Patient p = new Patient();
        p.setId(55L);
        p.setHealthId(healthId);

        Doctor d = new Doctor();
        d.setId(5L);
        d.setName("Doc Name");
        d.setEmail("doc@x.com");
        d.setSpecialization("General");

        MedicalRecord r = new MedicalRecord();
        r.setId(200L);
        r.setPatient(p);
        r.setDoctor(d);
        r.setDiagnosis("Flu");
        r.setPrescriptions("Rest");
        r.setLabNotes("None");
        r.setComments("OK");
        r.setCreatedAt(OffsetDateTime.now());

        when(patientRepository.findAll()).thenReturn(List.of(p));
        when(repository.findByPatientOrderByCreatedAtDesc(p)).thenReturn(List.of(r));

        List<Map<String, Object>> list = service.listForPatient(healthId);
        assertEquals(1, list.size());
        Map<String,Object> m = list.get(0);
        assertEquals(200L, m.get("id"));
        assertEquals("Flu", m.get("diagnosis"));
        assertEquals("Rest", m.get("prescriptions"));
        assertEquals("None", m.get("labNotes"));
        assertEquals("OK", m.get("comments"));

        @SuppressWarnings("unchecked")
        Map<String,Object> doc = (Map<String,Object>) m.get("doctor");
        assertNotNull(doc);
        assertEquals(5L, doc.get("id"));
        assertEquals("Doc Name", doc.get("name"));
        assertEquals("doc@x.com", doc.get("email"));
        assertEquals("General", doc.get("specialization"));
    }
}
