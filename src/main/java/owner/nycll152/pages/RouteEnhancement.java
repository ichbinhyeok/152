package owner.nycll152.pages;

import java.util.List;

public record RouteEnhancement(
        List<String> nextActionChecklist,
        List<String> escalationTriggers,
        List<String> sourceIds
) {
    public RouteEnhancement {
        nextActionChecklist = nextActionChecklist == null ? List.of() : List.copyOf(nextActionChecklist);
        escalationTriggers = escalationTriggers == null ? List.of() : List.copyOf(escalationTriggers);
        sourceIds = sourceIds == null ? List.of() : List.copyOf(sourceIds);
    }
}
