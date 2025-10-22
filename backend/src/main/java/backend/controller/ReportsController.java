package backend.controller;

import backend.service.ReportsService;
import backend.model.Appointment;
import backend.repository.AppointmentRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.awt.Color;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = {"http://localhost:3000"})
public class ReportsController {
    private final ReportsService service;
    private final AppointmentRepository appointmentRepository;

    public ReportsController(ReportsService service, AppointmentRepository appointmentRepository) {
        this.service = service;
        this.appointmentRepository = appointmentRepository;
    }

    // JSON endpoints
    @GetMapping("/patients/registration")
    public Map<String,Object> patientRegistration(@RequestParam(required = false) String from,
                                                  @RequestParam(required = false) String to){
        LocalDate f = from!=null? LocalDate.parse(from): null;
        LocalDate t = to!=null? LocalDate.parse(to): null;
        return service.patientRegistrationSummary(f,t);
    }

    @GetMapping("/patients/demographics")
    public Map<String,Object> patientDemographics(){ return service.patientDemographics(); }

    @GetMapping("/doctors/appointment-load")
    public Map<String,Long> doctorLoad(){ return service.doctorAppointmentLoad(); }

    @GetMapping("/appointments/summary")
    public Map<String,Object> appointmentSummary(@RequestParam(defaultValue = "daily") String period){
        return service.appointmentSummary(period);
    }

    @GetMapping("/appointments/by-specialization")
    public Map<String,Long> specialization(){ return service.specializationWise(); }

    @GetMapping("/appointments/cancellations")
    public Map<String,Long> cancellations(){ return service.cancellationsByDay(); }

    // CSV exports
    @GetMapping(value="/export/patients/registration.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportPatientRegistrationCsv(@RequestParam(required = false) String from,
                                                               @RequestParam(required = false) String to){
        var data = patientRegistration(from,to);
        Object seriesObj = data.get("series");
        Map<?,?> series = seriesObj instanceof Map ? (Map<?,?>) seriesObj : Map.of();
        String csv = "date,count\n" + series.entrySet().stream()
                .map(e-> String.valueOf(e.getKey())+","+ String.valueOf(e.getValue()))
                .collect(Collectors.joining("\n"));
        return csvResponse(csv, "patient-registration.csv");
    }

    @GetMapping(value="/export/patients/demographics.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportPatientDemographics(){
    var data = patientDemographics();
    Object gObj = data.get("gender");
    Object aObj = data.get("ageBuckets");
    Map<?,?> gender = gObj instanceof Map ? (Map<?,?>) gObj : Map.of();
    Map<?,?> ages = aObj instanceof Map ? (Map<?,?>) aObj : Map.of();
    String csv = new StringBuilder()
        .append("section,key,count\n")
        .append(gender.entrySet().stream().map(e->"gender,"+String.valueOf(e.getKey())+","+String.valueOf(e.getValue())).collect(Collectors.joining("\n")))
        .append("\n")
        .append(ages.entrySet().stream().map(e->"age,"+String.valueOf(e.getKey())+","+String.valueOf(e.getValue())).collect(Collectors.joining("\n")))
        .toString();
        return csvResponse(csv, "patient-demographics.csv");
    }

    @GetMapping(value="/export/doctors/appointment-load.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportDoctorLoad(){
        var data = doctorLoad();
        String csv = "doctor,count\n" + data.entrySet().stream()
                .map(e-> e.getKey()+","+ e.getValue())
                .collect(Collectors.joining("\n"));
        return csvResponse(csv, "doctor-appointment-load.csv");
    }

    @GetMapping(value="/export/appointments/summary.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportApptSummary(@RequestParam(defaultValue = "daily") String period){
        var data = appointmentSummary(period);
        Object bucketsObj = data.get("buckets");
        Map<?,?> buckets = bucketsObj instanceof Map ? (Map<?,?>) bucketsObj : Map.of();
        String header = "bucket,PENDING,CONFIRMED,REJECTED,CANCELLED";
        String csv = header + "\n" + buckets.entrySet().stream().map(e -> {
            Object mObj = e.getValue();
            Map<?,?> m = mObj instanceof Map ? (Map<?,?>) mObj : Map.of();
            long p = toLong(m.get("PENDING"));
            long c = toLong(m.get("CONFIRMED"));
            long r = toLong(m.get("REJECTED"));
            long x = toLong(m.get("CANCELLED"));
            return String.valueOf(e.getKey())+","+p+","+c+","+r+","+x;
        }).collect(Collectors.joining("\n"));
        return csvResponse(csv, "appointment-summary-"+period+".csv");
    }

    @GetMapping(value="/export/appointments/by-specialization.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportBySpec(){
        var data = specialization();
        String csv = "specialization,count\n" + data.entrySet().stream().map(e-> String.valueOf(e.getKey())+","+String.valueOf(e.getValue())).collect(Collectors.joining("\n"));
        return csvResponse(csv, "appointments-by-specialization.csv");
    }

    @GetMapping(value="/export/appointments/cancellations.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportCancellations(){
        var data = cancellations();
        String csv = "date,cancellations\n" + data.entrySet().stream().map(e-> String.valueOf(e.getKey())+","+String.valueOf(e.getValue())).collect(Collectors.joining("\n"));
        return csvResponse(csv, "appointment-cancellations.csv");
    }

    private ResponseEntity<byte[]> csvResponse(String csv, String filename){
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    private long toLong(Object o){
        if(o == null) return 0L;
        if(o instanceof Number) return ((Number) o).longValue();
        try { return Long.parseLong(String.valueOf(o)); } catch (Exception e){ return 0L; }
    }

    // PDF: Export all appointment details as an official report
    @GetMapping(value = "/export/appointments.pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> exportAppointmentsPdf(){
        var appointments = appointmentRepository.findAll();
        byte[] pdf = buildAppointmentsPdf(appointments);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=appointments-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private byte[] buildAppointmentsPdf(java.util.List<Appointment> list){
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4.rotate(), 36, 36, 54, 36);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            // Header
            var titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            var subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Paragraph title = new Paragraph("Medi.Way — Appointments Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);
            Paragraph sub = new Paragraph("Generated: " + java.time.OffsetDateTime.now().toString(), subFont);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(12f);
            doc.add(sub);

            // Table
            PdfPTable table = new PdfPTable(new float[]{8f, 16f, 12f, 12f, 12f, 10f, 18f, 14f});
            table.setWidthPercentage(100);
            addHeaderCell(table, "ID");
            addHeaderCell(table, "Patient Health ID");
            addHeaderCell(table, "Patient Name");
            addHeaderCell(table, "Doctor");
            addHeaderCell(table, "Specialization");
            addHeaderCell(table, "Date");
            addHeaderCell(table, "Time");
            addHeaderCell(table, "Status");

            DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
            DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

            var cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

            for(Appointment a : list){
                table.addCell(new Phrase(String.valueOf(a.getId()), cellFont));
                table.addCell(new Phrase(a.getPatient()!=null? String.valueOf(a.getPatient().getHealthId()):"", cellFont));
                table.addCell(new Phrase(a.getPatient()!=null? String.valueOf(a.getPatient().getFullName()):"", cellFont));
                table.addCell(new Phrase(a.getDoctor()!=null? String.valueOf(a.getDoctor().getName()):"", cellFont));
                table.addCell(new Phrase(a.getDoctor()!=null? String.valueOf(a.getDoctor().getSpecialization()):"", cellFont));
                table.addCell(new Phrase(a.getDate()!=null? df.format(a.getDate()):"", cellFont));
                table.addCell(new Phrase(a.getTime()!=null? tf.format(a.getTime()):"", cellFont));
                table.addCell(new Phrase(a.getStatus()!=null? a.getStatus().name():"", cellFont));
            }

            doc.add(table);
            doc.close();
            return baos.toByteArray();
        }catch(Exception e){
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private void addHeaderCell(PdfPTable table, String text){
        var headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(new Color(0x18,0x4e,0x77));
        cell.setPadding(6f);
        table.addCell(cell);
    }
}
