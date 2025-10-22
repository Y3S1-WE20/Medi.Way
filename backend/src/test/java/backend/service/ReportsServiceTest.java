package backend.service;

import backend.model.Appointment;
import backend.model.Doctor;
import backend.model.Patient;
import backend.repository.AppointmentRepository;
import backend.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportsServiceTest {

    private PatientRepository patientRepo;
    private AppointmentRepository apptRepo;
    private ReportsService service;

    @BeforeEach
    void setUp(){
        patientRepo = mock(PatientRepository.class);
        apptRepo = mock(AppointmentRepository.class);
        service = new ReportsService(patientRepo, apptRepo);
    }

    @Test
    void patientRegistrationSummary_countsWithinRange(){
        // two patients created today and yesterday
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Patient p1 = new Patient(); p1.setCreatedAt(now);
        Patient p2 = new Patient(); p2.setCreatedAt(now.minusDays(1));
        when(patientRepo.findAll()).thenReturn(List.of(p1,p2));

        var res = service.patientRegistrationSummary(now.toLocalDate().minusDays(1), now.toLocalDate());
        assertNotNull(res.get("series"));
        Map<?,?> series = (Map<?,?>) res.get("series");
        assertEquals(2, series.values().stream().mapToLong(v-> Long.parseLong(String.valueOf(v))).sum());
    }

    @Test
    void patientDemographics_countsGenderAndAges(){
        Patient a = new Patient(); a.setGender("male"); a.setDateOfBirth(LocalDate.now().minusYears(20));
        Patient b = new Patient(); b.setGender("female"); b.setDateOfBirth(LocalDate.now().minusYears(40));
        Patient c = new Patient(); c.setGender(null); // unknown
        when(patientRepo.findAll()).thenReturn(List.of(a,b,c));

        Map<String,Object> res = service.patientDemographics();
        Map<?,?> gender = (Map<?,?>) res.get("gender");
        assertEquals(1L, gender.get("MALE"));
        assertEquals(1L, gender.get("FEMALE"));
        assertEquals(1L, gender.get("UNKNOWN"));

        Map<?,?> ages = (Map<?,?>) res.get("ageBuckets");
        // buckets should sum to 2 because one patient has no dob
        long total = ages.values().stream().mapToLong(v-> ((Number)v).longValue()).sum();
        assertEquals(2L, total);
    }

    @Test
    void doctorAppointmentLoad_groupsByDoctorName(){
        Doctor d1 = new Doctor(); d1.setId(1L); d1.setName("A");
        Doctor d2 = new Doctor(); d2.setId(2L); d2.setName("B");
        Appointment a1 = new Appointment(); a1.setDoctor(d1);
        Appointment a2 = new Appointment(); a2.setDoctor(d1);
        Appointment a3 = new Appointment(); a3.setDoctor(d2);
        when(apptRepo.findAll()).thenReturn(List.of(a1,a2,a3));

        Map<String,Long> res = service.doctorAppointmentLoad();
        assertEquals(2L, res.get("A"));
        assertEquals(1L, res.get("B"));
    }

    @Test
    void appointmentSummary_bucketsByPeriod(){
        Appointment a = new Appointment(); a.setDate(LocalDate.of(2025,10,22)); a.setStatus(Appointment.Status.PENDING);
        when(apptRepo.findAll()).thenReturn(List.of(a));

        Map<String,Object> res = service.appointmentSummary("daily");
        Map<?,?> buckets = (Map<?,?>) res.get("buckets");
        assertTrue(buckets.containsKey("2025-10-22"));
        Map<?,?> bucket = (Map<?,?>) buckets.get("2025-10-22");
        assertEquals(1L, bucket.get("PENDING"));
    }

    @Test
    void specializationWise_groupsBySpecialization(){
        Doctor d1 = new Doctor(); d1.setSpecialization("Cardio");
        Doctor d2 = new Doctor(); d2.setSpecialization(null);
        Appointment a1 = new Appointment(); a1.setDoctor(d1);
        Appointment a2 = new Appointment(); a2.setDoctor(d2);
        when(apptRepo.findAll()).thenReturn(List.of(a1,a2));

        Map<String,Long> res = service.specializationWise();
        assertEquals(1L, res.get("Cardio"));
        assertEquals(1L, res.get("Unknown"));
    }

    @Test
    void cancellationsByDay_countsOnlyCancelled(){
        Appointment a = new Appointment(); a.setStatus(Appointment.Status.CANCELLED); a.setDate(LocalDate.of(2025,10,22));
        Appointment b = new Appointment(); b.setStatus(Appointment.Status.PENDING); b.setDate(LocalDate.of(2025,10,22));
        when(apptRepo.findAll()).thenReturn(List.of(a,b));
        Map<String,Long> res = service.cancellationsByDay();
        assertEquals(1L, res.get("2025-10-22"));
    }
}
