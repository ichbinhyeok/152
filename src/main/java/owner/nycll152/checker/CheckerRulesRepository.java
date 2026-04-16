package owner.nycll152.checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import owner.nycll152.config.AppProperties;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;

@Repository
public class CheckerRulesRepository {

    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    public CheckerRulesRepository(ObjectMapper objectMapper, AppProperties appProperties) {
        this.objectMapper = objectMapper;
        this.appProperties = appProperties;
    }

    public CheckerRules load() {
        try {
            return objectMapper.readValue(appProperties.checkerRulesPath().toFile(), CheckerRules.class);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read checker rules from " + appProperties.checkerRulesPath(), exception);
        }
    }
}
