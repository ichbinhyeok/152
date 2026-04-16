package owner.nycll152.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import owner.nycll152.config.AppProperties;

@RestController
public class RobotsController {

    private final AppProperties appProperties;

    public RobotsController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String robots() {
        if (!appProperties.isPublicIndexingEnabled()) {
            return """
                    User-agent: *
                    Disallow: /
                    """;
        }

        return """
                User-agent: *
                Disallow: /admin
                Sitemap: %s
                """.formatted(appProperties.canonicalUrl("/sitemap.xml"));
    }
}
