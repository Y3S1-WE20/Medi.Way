package backend.service;

import backend.model.Appointment;
import backend.model.Doctor;
import backend.model.Patient;
import backend.repository.AppointmentRepository;
import backend.repository.DoctorRepository;
import backend.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository apptRepo;
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;

    public AppointmentService(AppointmentRepository apptRepo, DoctorRepository doctorRepo, PatientRepository patientRepo) {
        this.apptRepo = apptRepo;
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
    }

    public List<LocalTime> availableSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepo.findById(doctorId).orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        List<Appointment> day = apptRepo.findByDoctorAndDate(doctor, date);
        // Simple 9:00-17:00 30-min slots
        List<LocalTime> slots = new ArrayList<>();
        for(LocalTime t = LocalTime.of(9,0); t.isBefore(LocalTime.of(17,0)); t = t.plusMinutes(30)) {
            LocalTime finalT = t;
            boolean taken = day.stream().anyMatch(a -> a.getTime().equals(finalT) && a.getStatus() != Appointment.Status.REJECTED && a.getStatus() != Appointment.Status.CANCELLED);
            if(!taken) slots.add(t);
        }
        return slots;
    }

    public Appointment book(String patientHealthId, Long doctorId, LocalDate date, LocalTime time) {
        Patient patient = patientRepo.findAll().stream().filter(p->patientHealthId.equals(p.getHealthId())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        Doctor doctor = doctorRepo.findById(doctorId).orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        if (apptRepo.existsByDoctorAndDateAndTime(doctor, date, time)) {
            throw new IllegalArgumentException("Slot already booked");
        }
        Appointment a = new Appointment();
        a.setPatient(patient);
        a.setDoctor(doctor);
        a.setDate(date);
        a.setTime(time);
        a.setStatus(Appointment.Status.PENDING);
        return apptRepo.save(a);
    }

    public List<Appointment> byPatient(String patientHealthId) {
        Patient patient = patientRepo.findAll().stream().filter(p->patientHealthId.equals(p.getHealthId())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        return apptRepo.findByPatient(patient);
    }

    public Appointment updatePatientAppointment(Long id, LocalDate date, LocalTime time) {
        Appointment a = apptRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        if (a.getStatus() == Appointment.Status.CONFIRMED) {
            throw new IllegalArgumentException("Cannot change a confirmed appointment");
        }
        if (date != null) a.setDate(date);
        if (time != null) a.setTime(time);
        a.setStatus(Appointment.Status.PENDING);
        return apptRepo.save(a);
    }

    public Appointment cancel(Long id) {
        Appointment a = apptRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        a.setStatus(Appointment.Status.CANCELLED);
        return apptRepo.save(a);
    }

    public Appointment setStatus(Long id, Appointment.Status status) {
        Appointment a = apptRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        a.setStatus(status);
        return apptRepo.save(a);
    }

    public List<Appointment> listAll() { return apptRepo.findAll(); }
    public List<Appointment> listByStatus(Appointment.Status status) { return apptRepo.findByStatus(status); }
}
