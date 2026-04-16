package owner.nycll152.ops;

import owner.nycll152.leads.LeadEventRecord;
import owner.nycll152.leads.LeadRecord;

import java.time.OffsetDateTime;
import java.util.List;

public record AdminSummary(
        OffsetDateTime generatedAt,
        long totalRoutes,
        long indexableRoutes,
        long heldRoutes,
        long checkerStarts,
        long checkerCompletions,
        long ctaClicks,
        long leadSubmissions,
        List<RouteStatusRecord> topRoutes,
        List<RouteStatusRecord> staleRoutes,
        PromotionReview promotionReview,
        List<LeadRecord> recentLeads,
        List<LeadEventRecord> recentEvents
) {
}
