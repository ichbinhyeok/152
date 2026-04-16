package owner.nycll152.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.nio.file.Path;
import java.util.Locale;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private static final String DEFAULT_ADMIN_PASSWORD = "change-me-before-deploy";

    private URI baseUrl = URI.create("http://localhost:8080");
    private Path dataRoot = Path.of("data");
    private Path storageRoot = Path.of("storage");
    private boolean publicIndexingEnabled;
    private final Admin admin = new Admin();
    private final RateLimit rateLimit = new RateLimit();

    @PostConstruct
    void validateDeploymentConfiguration() {
        if (!publicIndexingEnabled) {
            return;
        }

        String scheme = baseUrl.getScheme() == null ? "" : baseUrl.getScheme().toLowerCase(Locale.ROOT);
        String host = baseUrl.getHost() == null ? "" : baseUrl.getHost().toLowerCase(Locale.ROOT);

        if (!"https".equals(scheme)) {
            throw new IllegalStateException("Public indexing requires app.base-url to use https.");
        }

        if (host.isBlank() || host.equals("localhost") || host.equals("127.0.0.1")) {
            throw new IllegalStateException("Public indexing requires a non-localhost app.base-url.");
        }

        if (admin.password == null || admin.password.isBlank() || DEFAULT_ADMIN_PASSWORD.equals(admin.password)) {
            throw new IllegalStateException("Public indexing requires a non-default app.admin.password.");
        }
    }

    public URI getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URI baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Path getDataRoot() {
        return dataRoot;
    }

    public void setDataRoot(Path dataRoot) {
        this.dataRoot = dataRoot;
    }

    public Path getStorageRoot() {
        return storageRoot;
    }

    public void setStorageRoot(Path storageRoot) {
        this.storageRoot = storageRoot;
    }

    public boolean isPublicIndexingEnabled() {
        return publicIndexingEnabled;
    }

    public void setPublicIndexingEnabled(boolean publicIndexingEnabled) {
        this.publicIndexingEnabled = publicIndexingEnabled;
    }

    public Admin getAdmin() {
        return admin;
    }

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public Path routeInventoryPath() {
        return dataRoot.resolve(Path.of("derived", "routes.json"));
    }

    public Path checkerRulesPath() {
        return dataRoot.resolve(Path.of("normalized", "ll152", "checker-rules.json"));
    }

    public Path sourceDirectoryPath() {
        return dataRoot.resolve(Path.of("normalized", "sources"));
    }

    public Path routeStatusPath() {
        return dataRoot.resolve(Path.of("ops", "route-status.csv"));
    }

    public Path promotionReviewPath() {
        return dataRoot.resolve(Path.of("ops", "promotion-review.json"));
    }

    public Path adminSnapshotPath() {
        return dataRoot.resolve(Path.of("ops", "admin-metrics-snapshot.json"));
    }

    public Path leadsDirectory() {
        return storageRoot.resolve("leads");
    }

    public Path leadsPath() {
        return leadsDirectory().resolve("leads.csv");
    }

    public Path leadEventsPath() {
        return leadsDirectory().resolve("lead_events.csv");
    }

    public String canonicalUrl(String path) {
        return baseUrl.resolve(path).toString();
    }

    public static class Admin {
        private String username = "admin";
        private String password = DEFAULT_ADMIN_PASSWORD;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class RateLimit {
        private boolean enabled = true;
        private int checkerPerMinute = 60;
        private int leadsPerHour = 12;
        private int eventsPerMinute = 180;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getCheckerPerMinute() {
            return checkerPerMinute;
        }

        public void setCheckerPerMinute(int checkerPerMinute) {
            this.checkerPerMinute = checkerPerMinute;
        }

        public int getLeadsPerHour() {
            return leadsPerHour;
        }

        public void setLeadsPerHour(int leadsPerHour) {
            this.leadsPerHour = leadsPerHour;
        }

        public int getEventsPerMinute() {
            return eventsPerMinute;
        }

        public void setEventsPerMinute(int eventsPerMinute) {
            this.eventsPerMinute = eventsPerMinute;
        }
    }
}
