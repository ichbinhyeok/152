package owner.nycll152.pages;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RouteService {

    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public RoutePage requireBySlug(String slug) {
        return routeRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<RoutePage> coreRoutes() {
        return routeRepository.loadAll().stream()
                .filter(RoutePage::indexable)
                .filter(routePage -> !isTrustRoute(routePage))
                .filter(routePage -> !routePage.slug().equals("ll152-checker"))
                .toList();
    }

    public List<RoutePage> trustRoutes() {
        return routeRepository.loadAll().stream()
                .filter(RoutePage::indexable)
                .filter(this::isTrustRoute)
                .toList();
    }

    public List<RoutePage> heldRoutes() {
        return routeRepository.loadAll().stream()
                .filter(routePage -> !routePage.indexable())
                .toList();
    }

    public List<String> sitemapPaths() {
        return java.util.stream.Stream.concat(
                        java.util.stream.Stream.of("/"),
                        routeRepository.loadAll().stream()
                                .filter(RoutePage::indexable)
                                .map(RoutePage::path)
                )
                .distinct()
                .toList();
    }

    public List<RoutePage> relatedRoutes(RoutePage routePage) {
        Map<String, RoutePage> pagesByPath = routeRepository.loadAll().stream()
                .collect(Collectors.toMap(RoutePage::path, route -> route));

        return routePage.relatedPaths().stream()
                .map(pagesByPath::get)
                .filter(route -> route != null)
                .toList();
    }

    public RoutePage districtOverlayPage(String district) {
        String normalizedDistrict = district.trim().toUpperCase(Locale.ROOT);
        return new RoutePage(
                "district-overlay-" + normalizedDistrict,
                "districts-" + normalizedDistrict,
                "/districts/" + normalizedDistrict + "/",
                "district-overlay",
                "support",
                "noindex",
                "Held support route",
                "District " + normalizedDistrict + " support notes",
                "Use district overlays only as support context after the checker narrows the filing path.",
                "This page stays out of the public index until district-specific demand clearly improves the checker or due-cycle guidance.",
                "https://images.unsplash.com/photo-1480714378408-67cf0d13bc1b?auto=format&fit=crop&w=1600&q=80",
                "Apartment buildings viewed from street level in New York City.",
                "Get filing help",
                "filing_help",
                List.of(
                        new Section(
                                "How to use this route",
                                List.of(
                                        "Treat district overlays as support context, not as the primary verdict page.",
                                        "Use the checker first, then verify whether this district creates a practical timing or routing nuance for your filing."
                                ),
                                List.of(
                                        "Confirm the current DOB cycle notice before acting on a district-only assumption.",
                                        "Keep the no-gas-piping versus no-active-gas-service split separate before filing."
                                )
                        )
                ),
                List.of("/ll152-checker/", "/2026-deadline/")
        );
    }

    public TrackedRoute requireTrackedRoute(String path) {
        if ("/".equals(path)) {
            return new TrackedRoute("home", "/");
        }

        Optional<RoutePage> routePage = routeRepository.findByPath(path);
        if (routePage.isPresent()) {
            return new TrackedRoute(routePage.get().id(), routePage.get().path());
        }

        if (isDistrictOverlayPath(path)) {
            return new TrackedRoute("district-overlay", "/districts/{district}/");
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown route path.");
    }

    public String routeIdFromPath(String path) {
        return requireTrackedRoute(path).routeId();
    }

    private boolean isTrustRoute(RoutePage routePage) {
        return "trust".equals(routePage.family());
    }

    private boolean isDistrictOverlayPath(String path) {
        return path != null && path.matches("^/districts/[^/]+/$");
    }

    public record TrackedRoute(String routeId, String routePath) {
    }
}
