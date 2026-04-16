package owner.nycll152.web;

import jakarta.validation.Valid;
import owner.nycll152.checker.CheckerRequest;
import owner.nycll152.checker.CheckerResult;
import owner.nycll152.checker.CheckerService;
import owner.nycll152.leads.LeadCaptureRequest;
import owner.nycll152.leads.LeadCaptureResponse;
import owner.nycll152.leads.LeadEventRequest;
import owner.nycll152.leads.LeadService;
import owner.nycll152.ops.OpsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final CheckerService checkerService;
    private final LeadService leadService;
    private final OpsService opsService;

    public ApiController(CheckerService checkerService, LeadService leadService, OpsService opsService) {
        this.checkerService = checkerService;
        this.leadService = leadService;
        this.opsService = opsService;
    }

    @PostMapping("/checker/run")
    public CheckerResult runChecker(@Valid @RequestBody CheckerRequest request) {
        opsService.incrementCheckerStarts("ll152-checker", "/ll152-checker/");
        CheckerResult result = checkerService.evaluate(request);
        opsService.incrementCheckerCompletions("ll152-checker", "/ll152-checker/");
        return result;
    }

    @PostMapping("/leads/capture")
    public LeadCaptureResponse captureLead(@Valid @RequestBody LeadCaptureRequest request) {
        return leadService.capture(request);
    }

    @PostMapping("/leads/event")
    public ResponseEntity<Void> recordEvent(@Valid @RequestBody LeadEventRequest request) {
        leadService.recordEvent(request);
        return ResponseEntity.accepted().build();
    }
}
