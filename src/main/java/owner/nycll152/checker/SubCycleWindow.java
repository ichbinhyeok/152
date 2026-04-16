package owner.nycll152.checker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SubCycleWindow(
        String subCycle,
        List<Integer> communityDistricts,
        LocalDate windowStart,
        LocalDate windowEnd
) {
    public SubCycleWindow {
        communityDistricts = communityDistricts == null ? List.of() : List.copyOf(communityDistricts);
    }

    public boolean containsDistrict(Integer district) {
        return district != null && communityDistricts.contains(district);
    }
}
