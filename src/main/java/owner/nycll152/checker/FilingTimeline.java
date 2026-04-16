package owner.nycll152.checker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FilingTimeline(
        int gps1ToOwnerDays,
        int gps2ToDobDays,
        int correctionAfterInspectionDays,
        int correctionWithAdditionalTimeDays,
        int inspectionExtensionDays
) {
}
