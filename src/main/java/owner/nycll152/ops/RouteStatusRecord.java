package owner.nycll152.ops;

public record RouteStatusRecord(
        String routeId,
        String routePath,
        String routeFamily,
        String phase,
        String indexStatus,
        String sourceFreshnessStatus,
        long checkerStarts,
        long checkerCompletions,
        long ctaClicks,
        long leadSubmissions,
        String promotionRecommendation,
        String recommendationReason
) {
    public RouteStatusRecord incrementCheckerStarts() {
        return new RouteStatusRecord(routeId, routePath, routeFamily, phase, indexStatus, sourceFreshnessStatus,
                checkerStarts + 1, checkerCompletions, ctaClicks, leadSubmissions, promotionRecommendation, recommendationReason);
    }

    public RouteStatusRecord incrementCheckerCompletions() {
        return new RouteStatusRecord(routeId, routePath, routeFamily, phase, indexStatus, sourceFreshnessStatus,
                checkerStarts, checkerCompletions + 1, ctaClicks, leadSubmissions, promotionRecommendation, recommendationReason);
    }

    public RouteStatusRecord incrementCtaClicks() {
        return new RouteStatusRecord(routeId, routePath, routeFamily, phase, indexStatus, sourceFreshnessStatus,
                checkerStarts, checkerCompletions, ctaClicks + 1, leadSubmissions, promotionRecommendation, recommendationReason);
    }

    public RouteStatusRecord incrementLeadSubmissions() {
        return new RouteStatusRecord(routeId, routePath, routeFamily, phase, indexStatus, sourceFreshnessStatus,
                checkerStarts, checkerCompletions, ctaClicks, leadSubmissions + 1, promotionRecommendation, recommendationReason);
    }
}
