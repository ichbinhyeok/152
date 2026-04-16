package owner.nycll152.pages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Section(
        String title,
        List<String> paragraphs,
        List<String> bullets
) {
    public Section {
        paragraphs = paragraphs == null ? List.of() : List.copyOf(paragraphs);
        bullets = bullets == null ? List.of() : List.copyOf(bullets);
    }
}
