package backend.service;

import backend.model.Appointment;
import backend.model.Doctor;
import backend.repository.AppointmentRepository;
import backend.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorServiceTest {

    private DoctorRepository repo;
    private AppointmentRepository apptRepo;
    private DoctorService service;

    @BeforeEach
    void setUp(){
        repo = mock(DoctorRepository.class);
        apptRepo = mock(AppointmentRepository.class);
        service = new DoctorService(repo, apptRepo);
    }

    @Test
    void create_throws_whenEmailExists(){
        when(repo.existsByEmail("a@b")) .thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.create("n","a@b","s", null));
    }

    @Test
    void create_saves_withPhoto_and_returns(){
        when(repo.existsByEmail("a@b")).thenReturn(false);
        Doctor saved = new Doctor(); saved.setId(10L);
        when(repo.save(any())).thenReturn(saved);

        MockMultipartFile photo = new MockMultipartFile("photo","p.png","image/png", new byte[]{1,2,3});
        Doctor out = null;
        try { out = service.create("n","a@b","s", photo); } catch(Exception e){ fail(e); }
        assertNotNull(out);
        assertEquals(10L, out.getId());
    }

    @Test
    void get_throws_whenNotFound_and_update_and_delete_forward(){
        when(repo.findById(5L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, ()-> service.get(5L));

        Doctor d = new Doctor(); d.setId(5L); d.setEmail("x@x");
        when(repo.findById(5L)).thenReturn(Optional.of(d));
        when(repo.existsByEmail("y@y")).thenReturn(false);
        try { service.update(5L, "N", "y@y", "spec", null); } catch(Exception e){ fail(e); }

        doNothing().when(repo).deleteById(6L);
        service.delete(6L);
        verify(repo).deleteById(6L);
    }

    @Test
    void setPassword_and_login_and_appointmentsByDoctor(){
        Doctor d = new Doctor(); d.setId(7L); d.setEmail("e@x");
        when(repo.findById(7L)).thenReturn(Optional.of(d));

        service.setPassword(7L, "secret");
        verify(repo).save(any(Doctor.class));

        // prepare a stored doctor with encoded password for login
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder enc = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        Doctor stored = new Doctor(); stored.setId(8L); stored.setEmail("login@x"); stored.setPasswordHash(enc.encode("pw"));
        when(repo.findByEmail("login@x")).thenReturn(Optional.of(stored));
        Doctor ok = service.login("login@x", "pw");
        assertEquals(stored.getId(), ok.getId());

        // appointmentsByDoctor
        Appointment a = new Appointment(); a.setDoctor(stored);
        when(apptRepo.findByDoctor(stored)).thenReturn(List.of(a));
        when(repo.findById(8L)).thenReturn(Optional.of(stored));
        var list = service.appointmentsByDoctor(8L);
        assertEquals(1, list.size());
    }
}
