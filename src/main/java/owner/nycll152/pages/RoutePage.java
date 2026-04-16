package owner.nycll152.pages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RoutePage(
        String id,
        String slug,
        String path,
        String family,
        String phase,
        String indexStatus,
        String eyebrow,
        String title,
        String summary,
        String hero,
        String imageUrl,
        String imageAlt,
        String ctaLabel,
        String ctaIntent,
        List<Section> sections,
        List<String> relatedPaths
) {
    public RoutePage {
        sections = sections == null ? List.of() : List.copyOf(sections);
        relatedPaths = relatedPaths == null ? List.of() : List.copyOf(relatedPaths);
    }

    public boolean indexable() {
        return "index".equalsIgnoreCase(indexStatus);
    }
}
