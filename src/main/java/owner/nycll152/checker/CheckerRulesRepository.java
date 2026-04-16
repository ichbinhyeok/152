package owner.nycll152.checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import owner.nycll152.config.AppDataLocator;
import owner.nycll152.config.AppProperties;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;

@Repository
public class CheckerRulesRepository {

    private final ObjectMapper objectMapper;
    private final AppDataLocator appDataLocator;

    public CheckerRulesRepository(ObjectMapper objectMapper, AppDataLocator appDataLocator) {
        this.objectMapper = objectMapper;
        this.appDataLocator = appDataLocator;
    }

    public CheckerRules load() {
        try (Reader reader = appDataLocator.openCheckerRulesReader()) {
            return objectMapper.readValue(reader, CheckerRules.class);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read checker rules.", exception);
        }
    }
}
