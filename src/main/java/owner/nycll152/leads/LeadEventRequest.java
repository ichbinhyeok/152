package owner.nycll152.leads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LeadEventRequest(
        @Size(max = 80) String routeId,
        @NotBlank @Size(max = 200) String routePath,
        @NotBlank @Size(max = 40) String eventType,
        @Size(max = 80) String scenarioKey,
        @Size(max = 200) String detail
) {
}
