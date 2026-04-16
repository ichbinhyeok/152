package owner.nycll152.leads;

import owner.nycll152.config.AppProperties;
import owner.nycll152.ops.OpsService;
import owner.nycll152.pages.RouteService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class LeadService {

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT;
    private static final CSVFormat CSV_READ_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .build();

    private final AppProperties appProperties;
    private final OpsService opsService;
    private final RouteService routeService;

    public LeadService(AppProperties appProperties, OpsService opsService, RouteService routeService) {
        this.appProperties = appProperties;
        this.opsService = opsService;
        this.routeService = routeService;
    }

    public LeadCaptureResponse capture(LeadCaptureRequest request) {
        String leadId = "lead-" + UUID.randomUUID();
        RouteService.TrackedRoute trackedRoute = routeService.requireTrackedRoute(request.routePath());

        ensureLeadFiles();

        try (CSVPrinter csvPrinter = new CSVPrinter(
                Files.newBufferedWriter(appProperties.leadsPath(), StandardOpenOption.CREATE, StandardOpenOption.APPEND),
                CSV_FORMAT
        )) {
            csvPrinter.printRecord(
                    sanitizeCsvField(leadId),
                    sanitizeCsvField(OffsetDateTime.now().toString()),
                    sanitizeCsvField(request.name()),
                    sanitizeCsvField(request.email()),
                    sanitizeCsvField(request.phone()),
                    sanitizeCsvField(request.buildingAddress()),
                    sanitizeCsvField(trackedRoute.routeId()),
                    sanitizeCsvField(request.routePath()),
                    sanitizeCsvField(request.intent()),
                    sanitizeCsvField(request.scenarioKey()),
                    sanitizeCsvField(request.message())
            );
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to write lead capture", exception);
        }

        opsService.recordLeadSubmission(trackedRoute.routeId(), trackedRoute.routePath());
        return new LeadCaptureResponse(leadId, "captured");
    }

    public void recordEvent(LeadEventRequest request) {
        RouteService.TrackedRoute trackedRoute = routeService.requireTrackedRoute(request.routePath());
        ensureLeadFiles();

        try (CSVPrinter csvPrinter = new CSVPrinter(
                Files.newBufferedWriter(appProperties.leadEventsPath(), StandardOpenOption.CREATE, StandardOpenOption.APPEND),
                CSV_FORMAT
        )) {
            csvPrinter.printRecord(
                    sanitizeCsvField("event-" + UUID.randomUUID()),
                    sanitizeCsvField(OffsetDateTime.now().toString()),
                    sanitizeCsvField(trackedRoute.routeId()),
                    sanitizeCsvField(request.routePath()),
                    sanitizeCsvField(request.eventType()),
                    sanitizeCsvField(request.scenarioKey()),
                    sanitizeCsvField(request.detail())
            );
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to write lead event", exception);
        }

        if ("cta_click".equals(request.eventType())) {
            opsService.incrementCtaClicks(trackedRoute.routeId(), trackedRoute.routePath());
        }
    }

    public List<LeadRecord> loadRecentLeads(int limit) {
        ensureLeadFiles();

        try (CSVParser csvParser = CSVParser.parse(appProperties.leadsPath(), StandardCharsets.UTF_8, CSV_READ_FORMAT)) {
            return csvParser.stream()
                    .map(this::toLeadRecord)
                    .sorted(Comparator.comparing(LeadRecord::createdAt).reversed())
                    .limit(limit)
                    .toList();
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read leads file", exception);
        }
    }

    public List<LeadEventRecord> loadRecentEvents(int limit) {
        ensureLeadFiles();

        try (CSVParser csvParser = CSVParser.parse(appProperties.leadEventsPath(), StandardCharsets.UTF_8, CSV_READ_FORMAT)) {
            return csvParser.stream()
                    .map(this::toLeadEventRecord)
                    .sorted(Comparator.comparing(LeadEventRecord::createdAt).reversed())
                    .limit(limit)
                    .toList();
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read lead events file", exception);
        }
    }

    public Path leadsExportPath() {
        ensureLeadFiles();
        return appProperties.leadsPath();
    }

    public Path leadEventsExportPath() {
        ensureLeadFiles();
        return appProperties.leadEventsPath();
    }

    private void ensureLeadFiles() {
        try {
            Files.createDirectories(appProperties.leadsDirectory());

            if (Files.notExists(appProperties.leadsPath())) {
                try (CSVPrinter csvPrinter = new CSVPrinter(Files.newBufferedWriter(appProperties.leadsPath()), CSV_FORMAT)) {
                    csvPrinter.printRecord(
                            "lead_id",
                            "created_at",
                            "name",
                            "email",
                            "phone",
                            "building_address",
                            "route_id",
                            "route_path",
                            "intent",
                            "scenario_key",
                            "message"
                    );
                }
            }

            if (Files.notExists(appProperties.leadEventsPath())) {
                try (CSVPrinter csvPrinter = new CSVPrinter(Files.newBufferedWriter(appProperties.leadEventsPath()), CSV_FORMAT)) {
                    csvPrinter.printRecord(
                            "event_id",
                            "created_at",
                            "route_id",
                            "route_path",
                            "event_type",
                            "scenario_key",
                            "detail"
                    );
                }
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to initialize lead storage", exception);
        }
    }

    private LeadRecord toLeadRecord(CSVRecord record) {
        return new LeadRecord(
                record.get("lead_id"),
                OffsetDateTime.parse(record.get("created_at")),
                record.get("name"),
                record.get("email"),
                record.get("phone"),
                record.get("building_address"),
                record.get("route_id"),
                record.get("route_path"),
                record.get("intent"),
                record.get("scenario_key"),
                record.get("message")
        );
    }

    private LeadEventRecord toLeadEventRecord(CSVRecord record) {
        return new LeadEventRecord(
                record.get("event_id"),
                OffsetDateTime.parse(record.get("created_at")),
                record.get("route_id"),
                record.get("route_path"),
                record.get("event_type"),
                record.get("scenario_key"),
                record.get("detail")
        );
    }

    private String sanitizeCsvField(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value
                .replace("\r\n", "\n")
                .replace('\r', '\n');

        if (!normalized.isEmpty() && List.of('=', '+', '-', '@').contains(normalized.charAt(0))) {
            return "'" + normalized;
        }

        return normalized;
    }
}
