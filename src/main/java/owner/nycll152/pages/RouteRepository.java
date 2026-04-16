package owner.nycll152.pages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import owner.nycll152.config.AppProperties;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@Repository
public class RouteRepository {

    private static final TypeReference<List<RoutePage>> ROUTE_LIST = new TypeReference<>() {};

    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    public RouteRepository(ObjectMapper objectMapper, AppProperties appProperties) {
        this.objectMapper = objectMapper;
        this.appProperties = appProperties;
    }

    public List<RoutePage> loadAll() {
        try {
            return objectMapper.readValue(Files.newBufferedReader(appProperties.routeInventoryPath()), ROUTE_LIST);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read route inventory from " + appProperties.routeInventoryPath(), exception);
        }
    }

    public Optional<RoutePage> findBySlug(String slug) {
        return loadAll().stream()
                .filter(routePage -> routePage.slug().equals(slug))
                .findFirst();
    }

    public Optional<RoutePage> findByPath(String path) {
        return loadAll().stream()
                .filter(routePage -> routePage.path().equals(path))
                .findFirst();
    }
}
