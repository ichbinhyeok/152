package owner.nycll152.checker;

import java.util.Arrays;

public enum BuildingProfile {
    UNKNOWN("unknown", "I need a general check"),
    ONE_OR_TWO_FAMILY("one_or_two_family", "1 or 2 family"),
    THREE_FAMILY("three_family", "3 family"),
    MULTIFAMILY("multifamily", "Multifamily"),
    MIXED_USE_OR_COMMERCIAL("mixed_use_or_commercial", "Mixed-use or commercial");

    private final String value;
    private final String label;

    BuildingProfile(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String value() {
        return value;
    }

    public String label() {
        return label;
    }

    public static BuildingProfile from(String value) {
        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }

        return Arrays.stream(values())
                .filter(profile -> profile.value.equalsIgnoreCase(value))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
