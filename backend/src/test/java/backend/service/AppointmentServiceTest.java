package backend.service;

import backend.model.Appointment;
import backend.model.Doctor;
import backend.model.Patient;
import backend.repository.AppointmentRepository;
import backend.repository.DoctorRepository;
import backend.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    private AppointmentRepository apptRepo;
    private DoctorRepository doctorRepo;
    private PatientRepository patientRepo;
    private AppointmentService service;

    @BeforeEach
    void setUp() {
        apptRepo = mock(AppointmentRepository.class);
        doctorRepo = mock(DoctorRepository.class);
        patientRepo = mock(PatientRepository.class);
        service = new AppointmentService(apptRepo, doctorRepo, patientRepo);
    }

    @Test
    void book_successful_whenSlotFree() {
        String healthId = "pid-123";
        Long doctorId = 1L;
        LocalDate date = LocalDate.of(2025, 10, 22);
        LocalTime time = LocalTime.of(10, 0);

        Patient patient = new Patient();
        patient.setId(11L);
        patient.setHealthId(healthId);
        patient.setFullName("Test Patient");

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setName("Dr Who");

        when(patientRepo.findAll()).thenReturn(List.of(patient));
        when(doctorRepo.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(apptRepo.existsByDoctorAndDateAndTime(doctor, date, time)).thenReturn(false);

        Appointment saved = new Appointment();
        saved.setId(99L);
        when(apptRepo.save(any(Appointment.class))).thenReturn(saved);

        Appointment result = service.book(healthId, doctorId, date, time);

        // verify returned appointment is the one saved by repository
        assertNotNull(result);
        assertEquals(99L, result.getId());

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(apptRepo).save(captor.capture());
        Appointment toSave = captor.getValue();
        assertEquals(patient, toSave.getPatient(), "Saved appointment should reference the patient");
        assertEquals(doctor, toSave.getDoctor(), "Saved appointment should reference the doctor");
        assertEquals(date, toSave.getDate(), "Saved appointment should contain requested date");
        assertEquals(time, toSave.getTime(), "Saved appointment should contain requested time");
        assertEquals(Appointment.Status.PENDING, toSave.getStatus(), "New bookings should be PENDING");
    }

    @Test
    void book_throws_whenSlotTaken() {
        String healthId = "pid-123";
        Long doctorId = 1L;
        LocalDate date = LocalDate.of(2025, 10, 22);
        LocalTime time = LocalTime.of(10, 0);

        Patient patient = new Patient();
        patient.setHealthId(healthId);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        when(patientRepo.findAll()).thenReturn(List.of(patient));
        when(doctorRepo.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(apptRepo.existsByDoctorAndDateAndTime(doctor, date, time)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.book(healthId, doctorId, date, time));
        assertEquals("Slot already booked", ex.getMessage());
        verify(apptRepo, never()).save(any());
    }

    @Test
    void availableSlots_excludesBookedAndIncludesFree() {
        Long doctorId = 5L;
        LocalDate date = LocalDate.of(2025, 10, 22);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Appointment taken = new Appointment();
        taken.setDoctor(doctor);
        taken.setDate(date);
        taken.setTime(LocalTime.of(9,30));
        taken.setStatus(Appointment.Status.CONFIRMED);

        Appointment cancelled = new Appointment();
        cancelled.setDoctor(doctor);
        cancelled.setDate(date);
        cancelled.setTime(LocalTime.of(10,0));
        cancelled.setStatus(Appointment.Status.CANCELLED);

        when(doctorRepo.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(apptRepo.findByDoctorAndDate(doctor, date)).thenReturn(List.of(taken, cancelled));

        List<LocalTime> slots = service.availableSlots(doctorId, date);

        // 9:00 slot should be free, 9:30 should be excluded (taken), 10:00 should be free because CANCELLED
        assertTrue(slots.contains(LocalTime.of(9,0)), "9:00 should be available");
        assertFalse(slots.contains(LocalTime.of(9,30)), "9:30 is taken and should not be available");
        assertTrue(slots.contains(LocalTime.of(10,0)), "Cancelled appointment should free the slot");
    }

    @Test
    void byPatient_returnsAppointments() {
        String healthId = "pid-123";
        Patient patient = new Patient(); patient.setHealthId(healthId);
        Appointment a = new Appointment(); a.setPatient(patient);
        when(patientRepo.findAll()).thenReturn(List.of(patient));
        when(apptRepo.findByPatient(patient)).thenReturn(List.of(a));

        List<Appointment> res = service.byPatient(healthId);
        assertEquals(1, res.size());
    }

    @Test
    void updatePatientAppointment_throws_whenConfirmed() {
        Appointment a = new Appointment(); a.setId(5L); a.setStatus(Appointment.Status.CONFIRMED);
        when(apptRepo.findById(5L)).thenReturn(Optional.of(a));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.updatePatientAppointment(5L, LocalDate.now(), LocalTime.of(9,0)));
        assertEquals("Cannot change a confirmed appointment", ex.getMessage());
    }

    @Test
    void updatePatientAppointment_updatesAndSaves() {
        Appointment a = new Appointment(); a.setId(6L); a.setStatus(Appointment.Status.PENDING);
        when(apptRepo.findById(6L)).thenReturn(Optional.of(a));
        when(apptRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        LocalDate d = LocalDate.of(2025,10,23);
        LocalTime t = LocalTime.of(11,0);
        Appointment out = service.updatePatientAppointment(6L, d, t);
        assertEquals(d, out.getDate());
        assertEquals(t, out.getTime());
        assertEquals(Appointment.Status.PENDING, out.getStatus());
    }

    @Test
    void cancel_setsCancelled_and_setStatus_setsGivenStatus() {
        Appointment a = new Appointment(); a.setId(7L); a.setStatus(Appointment.Status.PENDING);
        when(apptRepo.findById(7L)).thenReturn(Optional.of(a));
        when(apptRepo.save(any())).thenAnswer(i->i.getArgument(0));

        Appointment canceled = service.cancel(7L);
        assertEquals(Appointment.Status.CANCELLED, canceled.getStatus());

        when(apptRepo.findById(7L)).thenReturn(Optional.of(canceled));
        Appointment updated = service.setStatus(7L, Appointment.Status.REJECTED);
        assertEquals(Appointment.Status.REJECTED, updated.getStatus());
    }

    @Test
    void listAll_and_listByStatus_forwardToRepo() {
        Appointment a = new Appointment(); a.setId(8L); a.setStatus(Appointment.Status.PENDING);
        when(apptRepo.findAll()).thenReturn(List.of(a));
        when(apptRepo.findByStatus(Appointment.Status.PENDING)).thenReturn(List.of(a));

        assertEquals(1, service.listAll().size());
        assertEquals(1, service.listByStatus(Appointment.Status.PENDING).size());
    }
}
