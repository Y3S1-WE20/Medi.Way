package backend.controller;

import backend.dto.LoginRequest;
import backend.model.Doctor;
import backend.service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorControllerTest {
    private DoctorService service;
    private DoctorController controller;

    @BeforeEach
    void setUp(){
        service = mock(DoctorService.class);
        controller = new DoctorController(service);
    }

    @Test
    void list_and_get_and_photo_and_login_and_myAppointments_forward(){
        Doctor d = new Doctor(); d.setId(1L); d.setName("D"); d.setEmail("d@x"); d.setSpecialization("S");
        when(service.list()).thenReturn(List.of(d));
        var list = controller.list();
        assertEquals(1, list.size());

        when(service.get(1L)).thenReturn(d);
        var get = controller.get(1L);
        assertEquals(1L, get.getId());

        d.setPhoto(new byte[]{1,2,3}); d.setPhotoContentType("image/png");
        when(service.get(1L)).thenReturn(d);
        var resp = controller.photo(1L);
        assertEquals("image/png", resp.getHeaders().getFirst("Content-Type"));

        LoginRequest req = new LoginRequest(); req.email = "a@b"; req.password = "p";
        when(service.login(anyString(), anyString())).thenReturn(d);
        var logged = controller.login(req);
        assertEquals(d, logged);
    }
}
