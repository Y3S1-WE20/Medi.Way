package backend.service;

import backend.model.Appointment;
import backend.model.Patient;
import backend.repository.AppointmentRepository;
import backend.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReportsService {
    private final PatientRepository patientRepo;
    private final AppointmentRepository apptRepo;

    public ReportsService(PatientRepository patientRepo, AppointmentRepository apptRepo) {
        this.patientRepo = patientRepo;
        this.apptRepo = apptRepo;
    }

    public Map<String,Object> patientRegistrationSummary(LocalDate from, LocalDate to){
        List<Patient> patients = patientRepo.findAll();
        Map<LocalDate, Long> byDay = new TreeMap<>();
        LocalDate start = from != null ? from : LocalDate.now().minusDays(30);
        LocalDate end = to != null ? to : LocalDate.now();
        for(LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) byDay.put(d, 0L);
        for(Patient p: patients){
            OffsetDateTime cat = null;
            try{ cat = p.getCreatedAt(); }catch(Exception ignored){}
            if(cat==null) continue;
            LocalDate day = cat.atZoneSameInstant(ZoneId.systemDefault()).toLocalDate();
            if(!day.isBefore(start) && !day.isAfter(end)) byDay.merge(day, 1L, Long::sum);
        }
        Map<String,Long> series = byDay.entrySet().stream().collect(Collectors.toMap(e->e.getKey().toString(), Map.Entry::getValue, (a,b)->a, LinkedHashMap::new));
        return Map.of("range", List.of(start.toString(), end.toString()), "series", series);
    }

    public Map<String,Object> patientDemographics(){
        List<Patient> patients = patientRepo.findAll();
        // Gender
        Map<String, Long> gender = patients.stream()
                .map(p -> Optional.ofNullable(p.getGender()).orElse("UNKNOWN").toUpperCase())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        // Age buckets
        Map<String, Long> ages = new LinkedHashMap<>();
        ages.put("<18", 0L); ages.put("18-30", 0L); ages.put("31-45", 0L); ages.put("46-60", 0L); ages.put(">60", 0L);
        LocalDate today = LocalDate.now();
        for(Patient p: patients){
            LocalDate dob = p.getDateOfBirth();
            if(dob==null) continue;
            int age = today.getYear() - dob.getYear();
            if(today.getDayOfYear() < dob.getDayOfYear()) age--; // adjust birthday not reached
            if(age < 18) ages.merge("<18",1L,Long::sum);
            else if(age<=30) ages.merge("18-30",1L,Long::sum);
            else if(age<=45) ages.merge("31-45",1L,Long::sum);
            else if(age<=60) ages.merge("46-60",1L,Long::sum);
            else ages.merge(">60",1L,Long::sum);
        }
        return Map.of("gender", gender, "ageBuckets", ages);
    }

    public Map<String,Long> doctorAppointmentLoad(){
        List<Appointment> appts = apptRepo.findAll();
        return appts.stream().collect(Collectors.groupingBy(a -> a.getDoctor()!=null ? a.getDoctor().getName() : "Unknown", Collectors.counting()));
    }

    public Map<String,Object> appointmentSummary(String period){
        List<Appointment> appts = apptRepo.findAll();
        Map<String, Map<String, Long>> summary = new TreeMap<>();
        WeekFields wf = WeekFields.ISO;
        for(Appointment a: appts){
            LocalDate d = a.getDate();
            if(d==null) continue;
            String bucket;
            switch (period==null?"daily":period){
                case "weekly" -> bucket = d.getYear()+"-W"+String.format("%02d", d.get(wf.weekOfWeekBasedYear()));
                case "monthly" -> bucket = d.getYear()+"-"+String.format("%02d", d.getMonthValue());
                default -> bucket = d.toString();
            }
            summary.computeIfAbsent(bucket, k->new HashMap<>())
                    .merge(a.getStatus().name(), 1L, Long::sum);
        }
        return Map.of("period", period==null?"daily":period, "buckets", summary);
    }

    public Map<String,Long> specializationWise(){
        List<Appointment> appts = apptRepo.findAll();
        return appts.stream().collect(Collectors.groupingBy(a -> a.getDoctor()!=null? Optional.ofNullable(a.getDoctor().getSpecialization()).orElse("Unknown"):"Unknown", Collectors.counting()));
    }

    public Map<String,Long> cancellationsByDay(){
        List<Appointment> appts = apptRepo.findAll().stream().filter(a->a.getStatus()== Appointment.Status.CANCELLED).toList();
        return appts.stream().collect(Collectors.groupingBy(a-> a.getDate()!=null? a.getDate().toString():"Unknown", TreeMap::new, Collectors.counting()));
    }
}
