package owner.nycll152.sources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SourceRecord(
        String sourceId,
        String title,
        String sourceType,
        String status,
        String verifiedAt,
        String url,
        List<String> keyFacts
) {
    public SourceRecord {
        keyFacts = keyFacts == null ? List.of() : List.copyOf(keyFacts);
    }
}
