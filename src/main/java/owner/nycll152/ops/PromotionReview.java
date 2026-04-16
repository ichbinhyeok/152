package owner.nycll152.ops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PromotionReview(
        String updatedAt,
        String summary,
        List<String> heldRecommendations
) {
    public PromotionReview {
        heldRecommendations = heldRecommendations == null ? List.of() : List.copyOf(heldRecommendations);
    }
}
