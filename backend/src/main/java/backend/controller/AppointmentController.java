package backend.controller;

import backend.model.Appointment;
import backend.model.Doctor;
import backend.repository.DoctorRepository;
import backend.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000"})
public class AppointmentController {
    private final AppointmentService service;
    private final DoctorRepository doctorRepo;

    public AppointmentController(AppointmentService service, DoctorRepository doctorRepo) {
        this.service = service;
        this.doctorRepo = doctorRepo;
    }

    // Doctors search by specialization
    @GetMapping("/doctors/search")
    public List<Doctor> searchDoctors(@RequestParam String specialization) {
        return doctorRepo.findAll().stream().filter(d -> d.getSpecialization()!=null && d.getSpecialization().toLowerCase().contains(specialization.toLowerCase()))
                .map(d -> { Doctor x = new Doctor(); x.setId(d.getId()); x.setName(d.getName()); x.setEmail(d.getEmail()); x.setSpecialization(d.getSpecialization()); return x; })
                .toList();
    }

    // Available slots for a doctor on a date
    @GetMapping("/appointments/slots")
    public List<LocalTime> slots(@RequestParam Long doctorId,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.availableSlots(doctorId, date);
    }

    // Book appointment by patient healthId
    @PostMapping("/appointments/book")
    public Appointment book(@RequestParam String healthId,
                            @RequestParam Long doctorId,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {
        return service.book(healthId, doctorId, date, time);
    }

    // Patient's appointments
    @GetMapping("/appointments/mine")
    public List<Appointment> my(@RequestParam String healthId) {
        return service.byPatient(healthId);
    }

    // Patient updates an appointment (reschedule)
    @PutMapping("/appointments/{id}")
    public Appointment update(@PathVariable Long id,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {
        return service.updatePatientAppointment(id, date, time);
    }

    // Patient cancels
    @DeleteMapping("/appointments/{id}")
    public Appointment cancel(@PathVariable Long id) { return service.cancel(id); }

    // Admin confirm/reject
    @PostMapping("/admin/appointments/{id}/confirm")
    public Appointment confirm(@PathVariable Long id) { return service.setStatus(id, Appointment.Status.CONFIRMED); }
    @PostMapping("/admin/appointments/{id}/reject")
    public Appointment reject(@PathVariable Long id) { return service.setStatus(id, Appointment.Status.REJECTED); }

    // Admin: list appointments (optional filter by status)
    @GetMapping("/admin/appointments")
    public List<Appointment> adminList(@RequestParam(required = false) Appointment.Status status) {
        if (status == null) return service.listAll();
        return service.listByStatus(status);
    }
}
