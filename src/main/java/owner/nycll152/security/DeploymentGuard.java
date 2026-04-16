package owner.nycll152.security;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.stereotype.Component;
import owner.nycll152.config.AppDataLocator;
import owner.nycll152.config.AppProperties;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@Component
public class DeploymentGuard {

    private final AppProperties appProperties;
    private final AppDataLocator appDataLocator;

    public DeploymentGuard(AppProperties appProperties, AppDataLocator appDataLocator) {
        this.appProperties = appProperties;
        this.appDataLocator = appDataLocator;
    }

    @PostConstruct
    void validateIndexableRouteFreshness() {
        if (!appProperties.isPublicIndexingEnabled()) {
            return;
        }

        try (CSVParser csvParser = CSVParser.parse(
                appDataLocator.ensureRouteStatusFile(),
                StandardCharsets.UTF_8,
                CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build()
        )) {
            List<String> staleRoutes = csvParser.stream()
                    .filter(record -> "index".equalsIgnoreCase(record.get("index status")))
                    .filter(record -> !"current".equalsIgnoreCase(record.get("source freshness status")))
                    .map(record -> record.get("route path"))
                    .toList();

            if (!staleRoutes.isEmpty()) {
                throw new IllegalStateException("Public indexing is blocked because indexable routes are not current: " + staleRoutes);
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to validate route freshness before public indexing.", exception);
        }
    }
}
