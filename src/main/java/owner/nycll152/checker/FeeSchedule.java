package owner.nycll152.checker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FeeSchedule(
        int inspectionCertification,
        int extensionRequest,
        int correctionCertification,
        int noGasPipingCertification,
        int noGasServiceDocumentation
) {
}
