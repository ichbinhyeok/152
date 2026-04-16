package owner.nycll152.sources;

import com.fasterxml.jackson.databind.ObjectMapper;
import owner.nycll152.config.AppProperties;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class SourceRepository {

    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    public SourceRepository(ObjectMapper objectMapper, AppProperties appProperties) {
        this.objectMapper = objectMapper;
        this.appProperties = appProperties;
    }

    public List<SourceRecord> loadAll() {
        try (Stream<Path> paths = Files.list(appProperties.sourceDirectoryPath())) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .map(this::readSource)
                    .toList();
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read sources from " + appProperties.sourceDirectoryPath(), exception);
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

    private SourceRecord readSource(Path path) {
        try (var reader = Files.newBufferedReader(path)) {
            return objectMapper.readValue(reader, SourceRecord.class);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read source file " + path, exception);
        }
    }
}
