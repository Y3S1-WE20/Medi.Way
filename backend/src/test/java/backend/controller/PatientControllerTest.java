package backend.controller;

import backend.model.Patient;
import backend.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientControllerTest {

    private PatientService service;
    private PatientController controller;

    @BeforeEach
    void setUp(){
        service = mock(PatientService.class);
        controller = new PatientController(service);
    }

    @Test
    void getQr_and_getPatient_work() throws Exception {
        BufferedImage img = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
        when(service.qrForHealthId("h1")).thenReturn(img);

        ResponseEntity<byte[]> resp = controller.getQr("h1", false);
        assertEquals("image/png", resp.getHeaders().getContentType().toString());

    Patient p = new Patient(); p.setId(3L); p.setHealthId("h2"); p.setFullName("Fn"); p.setEmail("e@x");
    p.setPhone("+100"); p.setAddress("Addr"); p.setDateOfBirth(LocalDate.of(1990,1,1));
        when(service.findByHealthId("h2")).thenReturn(Optional.of(p));
        Object obj = controller.getPatient("h2");
        assertTrue(obj instanceof java.util.Map);
        var m = (java.util.Map<?,?>) obj;
        assertEquals(3L, m.get("id"));
    }
}
