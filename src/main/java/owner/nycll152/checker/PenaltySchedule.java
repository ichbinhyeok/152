package owner.nycll152.checker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PenaltySchedule(
        int threeFamilyBuilding,
        int allOtherBuildings
) {
}
