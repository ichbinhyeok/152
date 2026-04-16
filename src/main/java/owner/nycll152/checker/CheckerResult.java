package owner.nycll152.checker;

import owner.nycll152.sources.OfficialSourceLink;

import java.util.List;

public record CheckerResult(
        String coverageVerdict,
        String dueCycleVerdict,
        String nextStepTitle,
        String nextStepSummary,
        String recommendedRoute,
        String primaryCtaLabel,
        String primaryCtaIntent,
        String scenarioKey,
        String confidenceLabel,
        String confidenceReason,
        String reviewBoundary,
        List<String> nextActionChecklist,
        List<OfficialSourceLink> officialSources,
        List<String> rationale,
        String sourceNote
) {
}
