package owner.nycll152.checker;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import static org.springframework.util.StringUtils.hasText;

public record CheckerRequest(
        @Size(max = 200) String address,
        @Size(max = 32) String bin,
        @Size(max = 32) String bbl,
        @Size(max = 16) String dofClass,
        @Size(max = 40) String buildingType,
        @Min(1) @Max(18) Integer communityDistrict,
        Boolean gasPiping,
        Boolean activeGasService
) {

    @AssertTrue(message = "Add a district, DOF class, building profile, or gas-status detail before you run the verdict.")
    public boolean hasDecisionSignal() {
        return hasText(dofClass)
                || BuildingProfile.from(buildingType) != BuildingProfile.UNKNOWN
                || communityDistrict != null
                || gasPiping != null
                || activeGasService != null;
    }

    @AssertTrue(message = "Active gas service can only be set after you confirm gas piping is present.")
    public boolean isGasStatusConsistent() {
        return activeGasService == null || Boolean.TRUE.equals(gasPiping);
    }
}
