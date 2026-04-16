package owner.nycll152.checker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CheckerRules(
        String verifiedAt,
        int activeCycleYear,
        String activeSubCycle,
        List<String> exemptDofClassifications,
        List<SubCycleWindow> cycle2Schedule,
        FilingTimeline filingTimeline,
        PenaltySchedule penaltySchedule,
        FeeSchedule feeSchedule,
        List<String> sourceIds
) {
    public CheckerRules {
        exemptDofClassifications = exemptDofClassifications == null ? List.of() : List.copyOf(exemptDofClassifications);
        cycle2Schedule = cycle2Schedule == null ? List.of() : List.copyOf(cycle2Schedule);
        sourceIds = sourceIds == null ? List.of() : List.copyOf(sourceIds);
    }
}
