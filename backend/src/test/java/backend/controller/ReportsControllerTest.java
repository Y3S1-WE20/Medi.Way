package backend.controller;

import backend.model.Appointment;
import backend.model.Doctor;
import backend.model.Patient;
import backend.service.ReportsService;
import backend.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportsControllerTest {

    private ReportsService reportsService;
    private AppointmentRepository appointmentRepository;
    private ReportsController controller;

    @BeforeEach
    void setUp(){
        reportsService = mock(ReportsService.class);
        appointmentRepository = mock(AppointmentRepository.class);
        controller = new ReportsController(reportsService, appointmentRepository);
    }

    @Test
    void exportPatientRegistrationCsv_returnsCsvWithHeaderAndRows(){
        Map<String,Long> series = Map.of("2025-10-21", 2L, "2025-10-22", 3L);
        when(reportsService.patientRegistrationSummary(null,null)).thenReturn(Map.of("series", series, "range", List.of("2025-10-21","2025-10-22")));

        ResponseEntity<byte[]> resp = controller.exportPatientRegistrationCsv(null,null);
        assertEquals("text/csv", resp.getHeaders().getContentType().toString());
        assertTrue(resp.getHeaders().getFirst("Content-Disposition").contains("patient-registration.csv"));
        String body = new String(resp.getBody());
        assertTrue(body.startsWith("date,count"));
        assertTrue(body.contains("2025-10-21,2"));
    }

    @Test
    void exportApptSummary_handlesNumericAndStringCounts(){
        Map<String,Object> buckets = Map.of("2025-10-22", Map.of("PENDING", "1", "CONFIRMED", 2));
        when(reportsService.appointmentSummary("daily")).thenReturn(Map.of("period","daily","buckets", buckets));

        ResponseEntity<byte[]> resp = controller.exportApptSummary("daily");
        assertEquals("text/csv", resp.getHeaders().getContentType().toString());
        String csv = new String(resp.getBody());
        assertTrue(csv.contains("bucket,PENDING,CONFIRMED,REJECTED,CANCELLED"));
        assertTrue(csv.contains("2025-10-22,1,2,0,0"));
    }

    @Test
    void exportAppointmentsPdf_generatesPdfBytesAndHeader(){
        Doctor d = new Doctor(); d.setName("Dr X"); d.setSpecialization("Gen");
        Patient p = new Patient(); p.setHealthId("H1"); p.setFullName("Alice");
        Appointment a = new Appointment();
        a.setId(11L); a.setDoctor(d); a.setPatient(p); a.setDate(LocalDate.of(2025,10,22)); a.setTime(LocalTime.of(9,0)); a.setStatus(Appointment.Status.CONFIRMED);
        when(appointmentRepository.findAll()).thenReturn(List.of(a));

        ResponseEntity<byte[]> resp = controller.exportAppointmentsPdf();
        assertEquals("application/pdf", resp.getHeaders().getContentType().toString());
        assertTrue(resp.getHeaders().getFirst("Content-Disposition").contains("appointments-report.pdf"));
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().length > 100, "PDF output should be non-trivial");
    }
}
