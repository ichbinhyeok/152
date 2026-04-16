package owner.nycll152.leads;

import java.time.OffsetDateTime;

public record LeadEventRecord(
        String eventId,
        OffsetDateTime createdAt,
        String routeId,
        String routePath,
        String eventType,
        String scenarioKey,
        String detail
) {
}
