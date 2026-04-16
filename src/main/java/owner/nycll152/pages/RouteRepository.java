package owner.nycll152.pages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import owner.nycll152.config.AppDataLocator;
import owner.nycll152.config.AppProperties;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;

@Repository
public class RouteRepository {

    private static final TypeReference<List<RoutePage>> ROUTE_LIST = new TypeReference<>() {};

    private final ObjectMapper objectMapper;
    private final AppDataLocator appDataLocator;

    public RouteRepository(ObjectMapper objectMapper, AppDataLocator appDataLocator) {
        this.objectMapper = objectMapper;
        this.appDataLocator = appDataLocator;
    }

    public List<RoutePage> loadAll() {
        try (Reader reader = appDataLocator.openRouteInventoryReader()) {
            return objectMapper.readValue(reader, ROUTE_LIST);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read route inventory.", exception);
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
