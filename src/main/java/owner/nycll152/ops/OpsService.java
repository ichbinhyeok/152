package owner.nycll152.ops;

import com.fasterxml.jackson.databind.ObjectMapper;
import owner.nycll152.config.AppProperties;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

@Service
public class OpsService {

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .build();

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public OpsService(AppProperties appProperties, ObjectMapper objectMapper) {
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
    }

    public synchronized void incrementCheckerStarts(String routeId, String routePath) {
        updateRouteStatus(routeId, routePath, RouteStatusRecord::incrementCheckerStarts);
    }

    public synchronized void incrementCheckerCompletions(String routeId, String routePath) {
        updateRouteStatus(routeId, routePath, RouteStatusRecord::incrementCheckerCompletions);
    }

    public synchronized void incrementCtaClicks(String routeId, String routePath) {
        updateRouteStatus(routeId, routePath, RouteStatusRecord::incrementCtaClicks);
    }

    public synchronized void recordLeadSubmission(String routeId, String routePath) {
        updateRouteStatus(routeId, routePath, RouteStatusRecord::incrementLeadSubmissions);
    }

    public synchronized AdminSummary buildSummary() {
        List<RouteStatusRecord> routeStatuses = loadRouteStatuses();
        PromotionReview promotionReview = loadPromotionReview();

        try {
            refreshSnapshot(routeStatuses);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to write admin snapshot", exception);
        }

        return new AdminSummary(
                OffsetDateTime.now(),
                routeStatuses.size(),
                routeStatuses.stream().filter(record -> "index".equalsIgnoreCase(record.indexStatus())).count(),
                routeStatuses.stream().filter(record -> !"index".equalsIgnoreCase(record.indexStatus())).count(),
                routeStatuses.stream().mapToLong(RouteStatusRecord::checkerStarts).sum(),
                routeStatuses.stream().mapToLong(RouteStatusRecord::checkerCompletions).sum(),
                routeStatuses.stream().mapToLong(RouteStatusRecord::ctaClicks).sum(),
                routeStatuses.stream().mapToLong(RouteStatusRecord::leadSubmissions).sum(),
                routeStatuses.stream()
                        .sorted(Comparator.comparingLong(this::trafficScore).reversed())
                        .limit(5)
                        .toList(),
                routeStatuses.stream()
                        .filter(routeStatusRecord -> !"current".equalsIgnoreCase(routeStatusRecord.sourceFreshnessStatus()))
                        .toList(),
                promotionReview,
                List.of(),
                List.of()
        );
    }

    public synchronized List<RouteStatusRecord> loadRouteStatuses() {
        try (CSVParser csvParser = CSVParser.parse(appProperties.routeStatusPath(), StandardCharsets.UTF_8, CSV_FORMAT)) {
            return csvParser.stream()
                    .map(this::toRouteStatusRecord)
                    .toList();
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read route status file", exception);
        }
    }

    private void updateRouteStatus(String routeId, String routePath, UnaryOperator<RouteStatusRecord> updater) {
        List<RouteStatusRecord> routeStatuses = loadRouteStatuses();
        Map<String, RouteStatusRecord> indexedStatuses = new LinkedHashMap<>();
        routeStatuses.forEach(status -> indexedStatuses.put(status.routeId(), status));

        RouteStatusRecord current = indexedStatuses.getOrDefault(routeId, new RouteStatusRecord(
                routeId,
                routePath,
                "unknown",
                "phase1",
                "index",
                "unknown",
                0,
                0,
                0,
                0,
                "hold",
                "Created automatically during runtime."
        ));

        indexedStatuses.put(routeId, updater.apply(current));
        writeRouteStatuses(indexedStatuses.values().stream().toList());
    }

    private void writeRouteStatuses(List<RouteStatusRecord> routeStatuses) {
        try {
            Files.createDirectories(appProperties.routeStatusPath().getParent());
            try (CSVPrinter csvPrinter = new CSVPrinter(
                    Files.newBufferedWriter(
                            appProperties.routeStatusPath(),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING,
                            StandardOpenOption.WRITE
                    ),
                    CSVFormat.DEFAULT
            )) {
                csvPrinter.printRecord(
                        "route id",
                        "route path",
                        "route family",
                        "phase",
                        "index status",
                        "source freshness status",
                        "checker starts",
                        "checker completions",
                        "cta clicks",
                        "lead submissions",
                        "promotion recommendation",
                        "recommendation reason"
                );

                for (RouteStatusRecord routeStatus : routeStatuses) {
                    csvPrinter.printRecord(
                            routeStatus.routeId(),
                            routeStatus.routePath(),
                            routeStatus.routeFamily(),
                            routeStatus.phase(),
                            routeStatus.indexStatus(),
                            routeStatus.sourceFreshnessStatus(),
                            routeStatus.checkerStarts(),
                            routeStatus.checkerCompletions(),
                            routeStatus.ctaClicks(),
                            routeStatus.leadSubmissions(),
                            routeStatus.promotionRecommendation(),
                            routeStatus.recommendationReason()
                    );
                }
            }

            refreshSnapshot(routeStatuses);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to write route status file", exception);
        }
    }

    private void refreshSnapshot(List<RouteStatusRecord> routeStatuses) throws IOException {
        Map<String, Object> snapshot = Map.of(
                "updatedAt", OffsetDateTime.now().toString(),
                "checkerStarts", routeStatuses.stream().mapToLong(RouteStatusRecord::checkerStarts).sum(),
                "checkerCompletions", routeStatuses.stream().mapToLong(RouteStatusRecord::checkerCompletions).sum(),
                "ctaClicks", routeStatuses.stream().mapToLong(RouteStatusRecord::ctaClicks).sum(),
                "leadSubmissions", routeStatuses.stream().mapToLong(RouteStatusRecord::leadSubmissions).sum(),
                "indexableRoutes", routeStatuses.stream().filter(record -> "index".equalsIgnoreCase(record.indexStatus())).count(),
                "heldRoutes", routeStatuses.stream().filter(record -> !"index".equalsIgnoreCase(record.indexStatus())).count()
        );

        Files.createDirectories(appProperties.adminSnapshotPath().getParent());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(appProperties.adminSnapshotPath().toFile(), snapshot);
    }

    private PromotionReview loadPromotionReview() {
        try {
            return objectMapper.readValue(appProperties.promotionReviewPath().toFile(), PromotionReview.class);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read promotion review file", exception);
        }
    }

    private RouteStatusRecord toRouteStatusRecord(CSVRecord record) {
        return new RouteStatusRecord(
                record.get("route id"),
                record.get("route path"),
                record.get("route family"),
                record.get("phase"),
                record.get("index status"),
                record.get("source freshness status"),
                Long.parseLong(record.get("checker starts")),
                Long.parseLong(record.get("checker completions")),
                Long.parseLong(record.get("cta clicks")),
                Long.parseLong(record.get("lead submissions")),
                record.get("promotion recommendation"),
                record.get("recommendation reason")
        );
    }

    private long trafficScore(RouteStatusRecord routeStatusRecord) {
        return routeStatusRecord.checkerStarts()
                + routeStatusRecord.checkerCompletions()
                + routeStatusRecord.ctaClicks()
                + routeStatusRecord.leadSubmissions();
    }
}
