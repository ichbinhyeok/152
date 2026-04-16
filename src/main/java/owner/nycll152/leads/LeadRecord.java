package owner.nycll152.leads;

import java.time.OffsetDateTime;

public record LeadRecord(
        String leadId,
        OffsetDateTime createdAt,
        String name,
        String email,
        String phone,
        String buildingAddress,
        String routeId,
        String routePath,
        String intent,
        String scenarioKey,
        String message
) {
}
