package owner.nycll152.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import owner.nycll152.config.AppProperties;
import owner.nycll152.pages.RouteService;

@RestController
public class SitemapController {

    private final AppProperties appProperties;
    private final RouteService routeService;

    public SitemapController(AppProperties appProperties, RouteService routeService) {
        this.appProperties = appProperties;
        this.routeService = routeService;
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemap() {
        if (!appProperties.isPublicIndexingEnabled()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        for (String path : routeService.sitemapPaths()) {
            xml.append("  <url>\n");
            xml.append("    <loc>").append(escapeXml(appProperties.canonicalUrl(path))).append("</loc>\n");
            xml.append("  </url>\n");
        }

        xml.append("</urlset>\n");
        return xml.toString();
    }

    private String escapeXml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
