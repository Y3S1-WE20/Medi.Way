package backend.controller;

import backend.service.MedicalRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
public class MedicalRecordController {
    private final MedicalRecordService service;

    public MedicalRecordController(MedicalRecordService service) {
        this.service = service;
    }

    public static class AddRecordRequest {
        public Long doctorId;
        public String patientHealthId;
        public String diagnosis;
        public String prescriptions;
        public String labNotes;
        public String comments;
    }

    @PostMapping("/api/records")
    public Map<String, Object> add(@RequestBody AddRecordRequest req){
        var rec = service.addRecord(req.doctorId, req.patientHealthId, req.diagnosis, req.prescriptions, req.labNotes, req.comments);
        return Map.of("id", rec.getId());
    }

    @GetMapping("/api/patients/{healthId}/records")
    public List<Map<String,Object>> byPatient(@PathVariable String healthId){
        return service.listForPatient(healthId);
    }
}
