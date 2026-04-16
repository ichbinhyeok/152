package owner.nycll152.sources;

import com.fasterxml.jackson.databind.ObjectMapper;
import owner.nycll152.config.AppDataLocator;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class SourceRepository {

    private final ObjectMapper objectMapper;
    private final AppDataLocator appDataLocator;

    public SourceRepository(ObjectMapper objectMapper, AppDataLocator appDataLocator) {
        this.objectMapper = objectMapper;
        this.appDataLocator = appDataLocator;
    }

    public List<SourceRecord> loadAll() {
        try {
            return appDataLocator.sourceResources().stream()
                    .sorted(Comparator.comparing(resource -> resource.getFilename() == null ? "" : resource.getFilename()))
                    .map(this::readSource)
                    .toList();
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read packaged sources.", exception);
        }
    }

    public List<SourceRecord> findByIds(List<String> sourceIds) {
        Map<String, SourceRecord> sourcesById = loadAll().stream()
                .collect(Collectors.toMap(SourceRecord::sourceId, Function.identity()));

        return sourceIds.stream()
                .map(sourcesById::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private SourceRecord readSource(Resource resource) {
        try (var reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return objectMapper.readValue(reader, SourceRecord.class);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read source file " + resource.getFilename(), exception);
        }
    }
}
