package backend.controller;

import backend.model.Appointment;
import backend.model.Doctor;
import backend.service.AppointmentService;
import backend.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentControllerTest {

    private AppointmentService service;
    private DoctorRepository doctorRepo;
    private AppointmentController controller;

    @BeforeEach
    void setUp(){
        service = mock(AppointmentService.class);
        doctorRepo = mock(DoctorRepository.class);
        controller = new AppointmentController(service, doctorRepo);
    }

    @Test
    void searchDoctors_filtersBySpecialization_and_copiesFields(){
        Doctor d1 = new Doctor(); d1.setId(1L); d1.setName("A"); d1.setEmail("a@x"); d1.setSpecialization("Cardiology");
        Doctor d2 = new Doctor(); d2.setId(2L); d2.setName("B"); d2.setEmail("b@x"); d2.setSpecialization("Neurology");
        when(doctorRepo.findAll()).thenReturn(List.of(d1,d2));

        var res = controller.searchDoctors("cardio");
        assertEquals(1, res.size());
        var out = res.get(0);
        assertEquals(d1.getId(), out.getId());
        assertEquals(d1.getName(), out.getName());
        assertNull(out.getPhoto()); // controller excludes photo
    }

    @Test
    void slots_and_book_and_my_forwardToService(){
        LocalDate date = LocalDate.of(2025,10,22);
        LocalTime time = LocalTime.of(9,0);
        when(service.availableSlots(1L, date)).thenReturn(List.of(time));
        var s = controller.slots(1L, date);
        assertEquals(1, s.size());

        Appointment a = new Appointment(); a.setId(5L);
        when(service.book("h1", 1L, date, time)).thenReturn(a);
        var booked = controller.book("h1",1L,date,time);
        assertEquals(5L, booked.getId());

        when(service.byPatient("h1")).thenReturn(List.of(a));
        var mine = controller.my("h1");
        assertEquals(1, mine.size());
    }

    @Test
    void update_cancel_confirm_reject_and_adminList(){
        Appointment a = new Appointment(); a.setId(9L);
        when(service.updatePatientAppointment(9L, null, null)).thenReturn(a);
        assertEquals(9L, controller.update(9L, null, null).getId());

        when(service.cancel(9L)).thenReturn(a);
        assertEquals(9L, controller.cancel(9L).getId());

        when(service.setStatus(9L, Appointment.Status.CONFIRMED)).thenReturn(a);
        assertEquals(9L, controller.confirm(9L).getId());

        when(service.setStatus(9L, Appointment.Status.REJECTED)).thenReturn(a);
        assertEquals(9L, controller.reject(9L).getId());

        when(service.listAll()).thenReturn(List.of(a));
        assertEquals(1, controller.adminList(null).size());

        when(service.listByStatus(Appointment.Status.PENDING)).thenReturn(List.of(a));
        assertEquals(1, controller.adminList(Appointment.Status.PENDING).size());
    }
}
