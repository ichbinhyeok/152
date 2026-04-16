package owner.nycll152.leads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LeadCaptureRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @Size(max = 40) String phone,
        @NotBlank @Size(max = 200) String buildingAddress,
        @Size(max = 80) String routeId,
        @NotBlank @Size(max = 200) String routePath,
        @NotBlank @Size(max = 40) String intent,
        @Size(max = 80) String scenarioKey,
        @Size(max = 2000) String message
) {
}
