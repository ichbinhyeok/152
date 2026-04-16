package owner.nycll152.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import owner.nycll152.checker.BuildingProfile;
import owner.nycll152.checker.CheckerRules;
import owner.nycll152.checker.CheckerRulesRepository;
import owner.nycll152.config.AppProperties;
import owner.nycll152.leads.LeadService;
import owner.nycll152.ops.AdminSummary;
import owner.nycll152.ops.OpsService;
import owner.nycll152.pages.RoutePage;
import owner.nycll152.pages.RouteEnhancement;
import owner.nycll152.pages.RouteEnhancementService;
import owner.nycll152.pages.RouteService;
import owner.nycll152.sources.SourceRecord;
import owner.nycll152.sources.SourceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class SiteController {

    private static final String ORGANIZATION_NAME = "LL152 Guidance Desk";
    private static final String CONTACT_EMAIL = "shinhyeok22@gmail.com";

    private final RouteService routeService;
    private final OpsService opsService;
    private final AppProperties appProperties;
    private final LeadService leadService;
    private final RouteEnhancementService routeEnhancementService;
    private final SourceService sourceService;
    private final CheckerRulesRepository checkerRulesRepository;
    private final ObjectMapper objectMapper;

    public SiteController(
            RouteService routeService,
            OpsService opsService,
            AppProperties appProperties,
            LeadService leadService,
            RouteEnhancementService routeEnhancementService,
            SourceService sourceService,
            CheckerRulesRepository checkerRulesRepository,
            ObjectMapper objectMapper
    ) {
        this.routeService = routeService;
        this.opsService = opsService;
        this.appProperties = appProperties;
        this.leadService = leadService;
        this.routeEnhancementService = routeEnhancementService;
        this.sourceService = sourceService;
        this.checkerRulesRepository = checkerRulesRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        CheckerRules checkerRules = checkerRulesRepository.load();
        List<SourceRecord> checkerSources = sourceService.findByIds(checkerRules.sourceIds());
        String siteUrl = canonicalUrl("/", request);
        model.addAttribute("currentPath", "/");
        model.addAttribute("siteUrl", siteUrl);
        model.addAttribute("blockerCards", routeEnhancementService.homeBlockers());
        model.addAttribute("coreRoutes", routeService.coreRoutes());
        model.addAttribute("trustRoutes", routeService.trustRoutes());
        model.addAttribute("heldRoutes", routeService.heldRoutes());
        model.addAttribute("canonicalUrl", siteUrl);
        applyTeamTrust(model, checkerSources, true);
        model.addAttribute("organizationStructuredData", organizationStructuredData(siteUrl));
        model.addAttribute("websiteStructuredData", websiteStructuredData(siteUrl));
        return "pages/home";
    }

    @GetMapping("/ll152-checker/")
    public String checker(Model model, HttpServletRequest request) {
        RoutePage page = routeService.requireBySlug("ll152-checker");
        CheckerRules checkerRules = checkerRulesRepository.load();
        List<SourceRecord> checkerSources = sourceService.findByIds(checkerRules.sourceIds());
        String siteUrl = canonicalUrl("/", request);
        String canonicalUrl = canonicalUrl(page.path(), request);
        model.addAttribute("page", page);
        model.addAttribute("siteUrl", siteUrl);
        model.addAttribute("currentPath", page.path());
        model.addAttribute("relatedRoutes", routeService.relatedRoutes(page));
        model.addAttribute("profiles", BuildingProfile.values());
        model.addAttribute("canonicalUrl", canonicalUrl);
        String teamSourceCheckedAt = applyTeamTrust(model, checkerSources, true);
        model.addAttribute("breadcrumbStructuredData", breadcrumbStructuredData(
                List.of(
                        ListItem.of(1, "Home", siteUrl),
                        ListItem.of(2, "Checker", canonicalUrl)
                )
        ));
        model.addAttribute("webPageStructuredData", webPageStructuredData(page.title(), canonicalUrl, teamSourceCheckedAt));
        return "pages/checker";
    }

    @GetMapping("/admin")
    public String admin(Model model, HttpServletRequest request) {
        AdminSummary baseSummary = opsService.buildSummary();
        AdminSummary adminSummary = new AdminSummary(
                baseSummary.generatedAt(),
                baseSummary.totalRoutes(),
                baseSummary.indexableRoutes(),
                baseSummary.heldRoutes(),
                baseSummary.checkerStarts(),
                baseSummary.checkerCompletions(),
                baseSummary.ctaClicks(),
                baseSummary.leadSubmissions(),
                baseSummary.topRoutes(),
                baseSummary.staleRoutes(),
                baseSummary.promotionReview(),
                leadService.loadRecentLeads(12),
                leadService.loadRecentEvents(20)
        );
        model.addAttribute("currentPath", "/admin");
        model.addAttribute("summary", adminSummary);
        model.addAttribute("canonicalUrl", canonicalUrl("/admin", request));
        applyTeamTrust(model, List.of(), false);
        return "pages/admin";
    }

    @GetMapping("/{slug:[a-z0-9\\-]+}")
    public String routeWithoutTrailingSlash(@PathVariable String slug) {
        routeService.requireBySlug(slug);
        return "redirect:/" + slug + "/";
    }

    @GetMapping("/districts/{district}")
    public String districtWithoutTrailingSlash(@PathVariable String district) {
        routeService.districtOverlayPage(district);
        return "redirect:/districts/" + district + "/";
    }

    @GetMapping("/admin/download/leads.csv")
    public ResponseEntity<ByteArrayResource> downloadLeadsCsv() {
        return downloadFile(leadService.leadsExportPath(), "leads.csv", MediaType.parseMediaType("text/csv"));
    }

    @GetMapping("/admin/download/lead-events.csv")
    public ResponseEntity<ByteArrayResource> downloadLeadEventsCsv() {
        return downloadFile(leadService.leadEventsExportPath(), "lead-events.csv", MediaType.parseMediaType("text/csv"));
    }

    @GetMapping("/admin/download/route-status.csv")
    public ResponseEntity<ByteArrayResource> downloadRouteStatusCsv() {
        opsService.buildSummary();
        return downloadFile(appProperties.routeStatusPath(), "route-status.csv", MediaType.parseMediaType("text/csv"));
    }

    @GetMapping("/admin/download/admin-metrics-snapshot.json")
    public ResponseEntity<ByteArrayResource> downloadAdminSnapshotJson() {
        opsService.buildSummary();
        return downloadFile(appProperties.adminSnapshotPath(), "admin-metrics-snapshot.json", MediaType.APPLICATION_JSON);
    }

    @GetMapping("/districts/{district}/")
    public String district(@PathVariable String district, Model model, HttpServletRequest request) {
        RoutePage page = routeService.districtOverlayPage(district);
        RouteEnhancement routeEnhancement = routeEnhancementService.forPage(page);
        List<SourceRecord> officialSources = sourceService.findByIds(routeEnhancement.sourceIds());
        String siteUrl = canonicalUrl("/", request);
        String canonicalUrl = canonicalUrl(page.path(), request);
        model.addAttribute("page", page);
        model.addAttribute("currentPath", page.path());
        model.addAttribute("relatedRoutes", routeService.relatedRoutes(page));
        model.addAttribute("routeEnhancement", routeEnhancement);
        model.addAttribute("officialSources", officialSources);
        model.addAttribute("canonicalUrl", canonicalUrl);
        model.addAttribute("siteUrl", siteUrl);
        String teamSourceCheckedAt = applyTeamTrust(model, officialSources, true);
        applyRouteStructuredData(model, siteUrl, canonicalUrl, page.title(), teamSourceCheckedAt);
        return "pages/route";
    }

    @GetMapping("/{slug}/")
    public String route(@PathVariable String slug, Model model, HttpServletRequest request) {
        RoutePage page = routeService.requireBySlug(slug);
        RouteEnhancement routeEnhancement = routeEnhancementService.forPage(page);
        List<SourceRecord> officialSources = sourceService.findByIds(routeEnhancement.sourceIds());
        String siteUrl = canonicalUrl("/", request);
        String canonicalUrl = canonicalUrl(page.path(), request);
        model.addAttribute("page", page);
        model.addAttribute("currentPath", page.path());
        model.addAttribute("relatedRoutes", routeService.relatedRoutes(page));
        model.addAttribute("routeEnhancement", routeEnhancement);
        model.addAttribute("officialSources", officialSources);
        model.addAttribute("canonicalUrl", canonicalUrl);
        model.addAttribute("siteUrl", siteUrl);
        String teamSourceCheckedAt = applyTeamTrust(model, officialSources, true);
        applyRouteStructuredData(model, siteUrl, canonicalUrl, page.title(), teamSourceCheckedAt);
        return "pages/route";
    }

    private String applyTeamTrust(Model model, List<SourceRecord> sources, boolean showTeamTrust) {
        String teamSourceCheckedAt = latestVerifiedAt(sources);
        model.addAttribute("showTeamTrust", showTeamTrust);
        model.addAttribute("teamSourceCheckedAt", teamSourceCheckedAt);
        model.addAttribute("teamSourceCount", sources.size());
        return teamSourceCheckedAt;
    }

    private void applyRouteStructuredData(
            Model model,
            String siteUrl,
            String canonicalUrl,
            String pageTitle,
            String teamSourceCheckedAt
    ) {
        model.addAttribute("breadcrumbStructuredData", breadcrumbStructuredData(
                List.of(
                        ListItem.of(1, "Home", siteUrl),
                        ListItem.of(2, "Routes", siteUrl + "filing-next-step/"),
                        ListItem.of(3, pageTitle, canonicalUrl)
                )
        ));
        model.addAttribute("webPageStructuredData", webPageStructuredData(pageTitle, canonicalUrl, teamSourceCheckedAt));
    }

    private String latestVerifiedAt(List<SourceRecord> sources) {
        return sources.stream()
                .map(SourceRecord::verifiedAt)
                .filter(value -> value != null && !value.isBlank())
                .max(String::compareTo)
                .orElse("");
    }

    private String canonicalUrl(String path, HttpServletRequest request) {
        String configuredHost = appProperties.getBaseUrl().getHost() == null
                ? ""
                : appProperties.getBaseUrl().getHost().toLowerCase(Locale.ROOT);

        if (configuredHost.isBlank() || configuredHost.equals("localhost") || configuredHost.equals("127.0.0.1")) {
            return ServletUriComponentsBuilder.fromRequestUri(request)
                    .replacePath(path)
                    .replaceQuery(null)
                    .build()
                    .toUriString();
        }

        return appProperties.canonicalUrl(path);
    }

    private String organizationStructuredData(String siteUrl) {
        return jsonLd(Map.of(
                "@context", "https://schema.org",
                "@type", "Organization",
                "name", ORGANIZATION_NAME,
                "url", siteUrl,
                "description", "A virtual team publishing source-checked NYC Local Law 152 routing guidance.",
                "email", CONTACT_EMAIL,
                "contactPoint", List.of(Map.of(
                        "@type", "ContactPoint",
                        "email", CONTACT_EMAIL,
                        "contactType", "customer support"
                )),
                "knowsAbout", List.of(
                        "NYC Local Law 152",
                        "Gas piping inspections",
                        "GPS1",
                        "GPS2",
                        "No gas piping certification"
                )
        ));
    }

    private String websiteStructuredData(String siteUrl) {
        return jsonLd(Map.of(
                "@context", "https://schema.org",
                "@type", "WebSite",
                "name", ORGANIZATION_NAME,
                "url", siteUrl,
                "publisher", Map.of(
                        "@type", "Organization",
                        "name", ORGANIZATION_NAME
                )
        ));
    }

    private String breadcrumbStructuredData(List<ListItem> items) {
        return jsonLd(Map.of(
                "@context", "https://schema.org",
                "@type", "BreadcrumbList",
                "itemListElement", items.stream()
                        .map(item -> Map.of(
                                "@type", "ListItem",
                                "position", item.position(),
                                "name", item.name(),
                                "item", item.url()
                        ))
                        .toList()
        ));
    }

    private String webPageStructuredData(String pageTitle, String canonicalUrl, String teamSourceCheckedAt) {
        return jsonLd(Map.of(
                "@context", "https://schema.org",
                "@type", "WebPage",
                "name", pageTitle,
                "url", canonicalUrl,
                "dateModified", teamSourceCheckedAt,
                "publisher", Map.of(
                        "@type", "Organization",
                        "name", ORGANIZATION_NAME
                ),
                "about", "NYC Local Law 152"
        ));
    }

    private String jsonLd(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize structured data.", exception);
        }
    }

    private ResponseEntity<ByteArrayResource> downloadFile(Path path, String filename, MediaType mediaType) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentLength(bytes.length)
                    .body(new ByteArrayResource(bytes));
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read admin export.", exception);
        }
    }

    private record ListItem(int position, String name, String url) {
        private static ListItem of(int position, String name, String url) {
            return new ListItem(position, name, url);
        }
    }
}
