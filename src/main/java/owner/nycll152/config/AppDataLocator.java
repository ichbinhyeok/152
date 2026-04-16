package owner.nycll152.config;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Component
public class AppDataLocator {

    private final AppProperties appProperties;
    private final ResourceLoader resourceLoader;
    private final ResourcePatternResolver resourcePatternResolver;

    public AppDataLocator(AppProperties appProperties, ResourceLoader resourceLoader) {
        this.appProperties = appProperties;
        this.resourceLoader = resourceLoader;
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver(resourceLoader);
    }

    public Reader openRouteInventoryReader() throws IOException {
        return openDataReader("data/derived/routes.json", appProperties.hasExternalDataRoot() ? appProperties.routeInventoryPath() : null);
    }

    public Reader openCheckerRulesReader() throws IOException {
        return openDataReader("data/normalized/ll152/checker-rules.json", appProperties.hasExternalDataRoot() ? appProperties.checkerRulesPath() : null);
    }

    public List<Resource> sourceResources() throws IOException {
        if (appProperties.hasExternalDataRoot()) {
            try (Stream<Path> paths = Files.list(appProperties.sourceDirectoryPath())) {
                return paths
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".json"))
                        .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                        .<Resource>map(FileSystemResource::new)
                        .toList();
            }
        }

        return Arrays.stream(resourcePatternResolver.getResources("classpath:data/normalized/sources/*.json"))
                .filter(Resource::exists)
                .sorted(Comparator.comparing(resource -> {
                    try {
                        return resource.getFilename() == null ? "" : resource.getFilename();
                    } catch (Exception exception) {
                        return "";
                    }
                }))
                .toList();
    }

    public Path ensureRouteStatusFile() {
        return ensureSeededFile(appProperties.routeStatusPath(), "data/ops/route-status.csv");
    }

    public Path ensurePromotionReviewFile() {
        return ensureSeededFile(appProperties.promotionReviewPath(), "data/ops/promotion-review.json");
    }

    private Reader openDataReader(String classpathLocation, Path externalPath) throws IOException {
        if (externalPath != null) {
            return Files.newBufferedReader(externalPath, StandardCharsets.UTF_8);
        }

        Resource resource = resourceLoader.getResource("classpath:" + classpathLocation);
        if (!resource.exists()) {
            throw new IOException("Missing packaged data resource: " + classpathLocation);
        }

        return new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    private Path ensureSeededFile(Path target, String classpathLocation) {
        if (Files.exists(target)) {
            return target;
        }

        Resource resource = resourceLoader.getResource("classpath:" + classpathLocation);
        if (!resource.exists()) {
            throw new IllegalStateException("Missing packaged seed data: " + classpathLocation);
        }

        try {
            Files.createDirectories(target.getParent());
            try (var inputStream = resource.getInputStream()) {
                Files.copy(inputStream, target);
            }
            return target;
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to initialize runtime data file " + target, exception);
        }
    }
}
