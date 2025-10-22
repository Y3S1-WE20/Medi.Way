package backend.repository;

import backend.model.Appointment;
import backend.model.Doctor;
import backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import backend.model.Appointment.Status;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorAndDate(Doctor doctor, LocalDate date);
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByPatient(Patient patient);
    boolean existsByDoctorAndDateAndTime(Doctor doctor, LocalDate date, LocalTime time);
    List<Appointment> findByStatus(Status status);
}
